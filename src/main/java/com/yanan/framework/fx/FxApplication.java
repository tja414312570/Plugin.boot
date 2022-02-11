package com.yanan.framework.fx;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.yanan.framework.fx.process.field.FxFieldPostProcess;
import com.yanan.framework.fx.process.field.FxFieldProcess;
import com.yanan.framework.fx.process.method.FxMethodPostProcess;
import com.yanan.framework.fx.process.method.FxMethodProcess;
import com.yanan.framework.fx.process.method.LazyLoader;
import com.yanan.framework.plugin.PlugsFactory;
import com.yanan.framework.plugin.handler.PlugsHandler;
import com.yanan.utils.CollectionUtils;
import com.yanan.utils.IOUtils;
import com.yanan.utils.asserts.Assert;
import com.yanan.utils.reflect.ReflectUtils;
import com.yanan.utils.reflect.TypeToken;
import com.yanan.utils.resource.Resource;
import com.yanan.utils.resource.ResourceManager;
import com.yanan.utils.string.StringUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.event.EventTarget;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class FxApplication extends Application{
	protected abstract void startApp(Stage stage) throws Exception;
	protected Parent root;
	protected Object controller;
	protected Stage primaryStage;
	private FXMLLoader fxmlLoader;
	private Class<?> appClass;
	private FxApplication parent;
	
	private static ThreadLocal<FxApplication> currentFxApplication = new InheritableThreadLocal<>();
	public static FxApplication getCurrentFxApplication() {
		return currentFxApplication.get();
	}

	@SuppressWarnings("unchecked")
	public <T extends Parent> T getRootView() {
		return (T) root;
	}
	public Parent getRoot() {
		return root;
	}

	public FXMLLoader getFxmlLoader() {
		return fxmlLoader;
	}

	public Class<?> getAppClass() {
		return appClass;
	}
	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) controller;
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	private void buildView() throws IOException {
		
		ContextView contextView = appClass.getAnnotation(ContextView.class);
		String res ;
		fxmlLoader = new FXMLLoader();
		if (contextView != null) {
			res = contextView.value();
			if(StringUtil.isEmpty(res))
				res = "classpath*:**"+appClass.getSimpleName()+".fxml";
		}else {
			res = "classpath*:**"+appClass.getSimpleName()+".fxml";
		}
		fxmlLoader.setLocation(ResourceManager.getResource(res).getURI().toURL());
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
		root = fxmlLoader.load();
		Icon icon = appClass.getAnnotation(Icon.class);
		if(icon != null) {
			Resource resource = ResourceManager.getResource(icon.value());
			Image image = new Image(resource.getInputStream());
			primaryStage.getIcons().add(image);
		}
	}
	private void bind() throws Exception {
		//加载菜单资源
		bindMenus();
		this.controller = fxmlLoader.getController();
		bindView();
		bindListener();
	}
	private void bindMenus() throws IOException {
		Menus menus = appClass.getAnnotation(Menus.class);
		if(menus != null) {
			Resource resource = ResourceManager.getResource(menus.value());
			List<Menu> menuList = loadMenu(resource);
			MenuBar menubar = new MenuBar();
			menubar.getMenus().addAll(menuList);
			((BorderPane)root).setTop(menubar);
		}
	}
	public List<Menu> loadMenu(Resource resource) throws IOException {
		ConfigList configList = ConfigFactory.parseString(IOUtils.toString(resource.getInputStream()));
//		Menu menu = new Menu(name);
		return parseMenu(configList);
	}
	private List<Menu> parseMenu(ConfigList configList) {
		List<Menu> menuList = new ArrayList<>();
		configList.forEach(configListItem->{
			if(ConfigObject.class.isAssignableFrom(configListItem.getClass())) {
				Config menuConfig = ((ConfigObject) configListItem).toConfig();
				String name = menuConfig.getString("name");
				String id = menuConfig.getString("id",name);
				Menu menu = new Menu(name);
				menu.setId(id);
				addViewId(id,menu);
				menuList.add(menu);
				if(menuConfig.hasPath("child")) {
					ConfigList childList = menuConfig.getList("child");
					List<MenuItem> menus = parseMenuItem(childList);
					if(CollectionUtils.isNotEmpty(menus)) {
						menu.getItems().addAll(menus);
					}
				}
			}else{
				throw new RuntimeException("menu item must be object");
			}
		});
		return menuList;
	}
	
	private List<MenuItem> parseMenuItem(ConfigList configList) {
		List<MenuItem> menuList = new ArrayList<>();
		configList.forEach(configListItem->{
			if(ConfigObject.class.isAssignableFrom(configListItem.getClass())) {
				Config menuConfig = ((ConfigObject) configListItem).toConfig();
				String name = menuConfig.getString("name");
				String id = menuConfig.getString("id",name);
				MenuItem menu ;
				if(menuConfig.hasPath("child")) {
					menu = new Menu(name);
					ConfigList childList = menuConfig.getList("child");
					List<MenuItem> menus = parseMenuItem(childList);
					if(CollectionUtils.isNotEmpty(menus)) {
						((Menu)menu).getItems().addAll(menus);
					}
				}else {
					menu = new MenuItem(name);
				}
				addViewId(id,menu);
				menu.setId(id);
				menuList.add(menu);
			}else{
				throw new RuntimeException("menu item must be object");
			}
		});
		return menuList;
	}
	protected void addViewId(String id,EventTarget view) {
		if(StringUtil.isNotEmpty(id) && FxApplication.class.isAssignableFrom(this.getAppClass())) {
			if(fxmlLoader.getNamespace().containsKey(id)) {
				throw new RuntimeException("menu view id "+id+" is exists");
			}
			fxmlLoader.getNamespace().put(id, view);
		}
	}
	private void bindView() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Field[] fields = ReflectUtils.getAllFields(appClass,FxApplication.class);
		for(Field field: fields) {
				if(field.getAnnotation(Controller.class) != null) {
					setFieldValue(field,this,root);
				}
				if(field.getAnnotation(RootView.class) != null) {
					setFieldValue(field, this, root);
				}
				if(field.getAnnotation(PrimaryStage.class) != null) {
					setFieldValue(field, this, primaryStage);
				}
				FindViewById findViewById = field.getAnnotation(FindViewById.class);
				if(findViewById != null) {
					String name = field.getName();
					if(StringUtil.isNotEmpty(findViewById.value())) {
						name = findViewById.value();
					}
					Object view = findViewById(name);
					setFieldValue(field, this, view);
				}
				FindView findView = field.getAnnotation(FindView.class);
				if(findView != null) {
					String name = field.getName();
					if(StringUtil.isNotEmpty(findView.value())) {
						name = findView.value();
					}
					Object view = root.lookup(name);
					setFieldValue(field, this, view);
				}
				Annotation[] annotations = field.getAnnotations();
				LazyLoad lazyLoad = field.getAnnotation(LazyLoad.class);
				for(Annotation annotation : annotations) {
					FxFieldProcess<Annotation> listener = PlugsFactory.
							getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxFieldProcess<Annotation>>() {}.getTypeClass(),
									annotation.annotationType().getSimpleName());
					if(listener != null) {
						bindListener(listener,lazyLoad,field,annotation);
					}
				}
			
		}
	}
	private void setFieldValue(Field field, FxApplication fxApplication, Object value) {
		try {
			ReflectUtils.setFieldValue(field, this, value);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("failed to process field "+field+" value "+ value,e);
		}
		
	}

	public <T> T findViewByField(Field field) {
		return findViewById(getViewIdByField(field));
	}
	public static String getViewIdByField(Field field) {
		FindViewById findViewById = field.getAnnotation(FindViewById.class);
		Assert.isNotNull(findViewById);
		String name = field.getName();
		if(StringUtil.isNotEmpty(findViewById.value())) {
			name = findViewById.value();
		}
		return name;
	}
	@SuppressWarnings("unchecked")
	public <T> T findViewById(String id) {
		T t = (T) root.lookup("#"+id);
//		if(t == null) {
//			t = (T) root.lookupAll("#"+id);
//		}
		if(t == null) {
			t = (T) fxmlLoader.getNamespace().get(id);
		}
		if(t == null)
			throw new RuntimeException("could not found view by id ["+id+"] at application "+this.appClass.getName());
		return t;
	}
 	private void bindListener() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		Method[] methods = appClass.getMethods();
		for(Method method: methods) {
			LazyLoad lazyLoad = method.getAnnotation(LazyLoad.class);
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation : annotations) {
				FxMethodProcess<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxMethodProcess<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					bindListener(listener,lazyLoad,method,annotation);
				}
			}
		}
	}

 	

	public void callDrawUi(Runnable execute) {
 		 Platform.runLater(execute);
 	}
 	
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		appClass = getClass();
		if(PlugsFactory.isProxy(this)) {
			PlugsHandler plugsHandler = PlugsFactory.getPluginsHandler(this);
			appClass = plugsHandler.getRegisterDefinition().getRegisterClass();
		}
		boolean isMain = true;
		if(stage.getScene() == null) {
			currentFxApplication.set(this);
			buildView();
			Scene scence = new Scene(root);
			stage.setScene(scence);
		}else {
			parent = currentFxApplication.get();
			this.fxmlLoader = parent.fxmlLoader;
			this.root = parent.root;
			isMain = false;
		}
		bind();
		this.startApp(stage);
		bindPost();
		if(isMain) {
			stage.show();
		}
	}
	private void bindPost() throws Exception{
		fieldPostProcess();
		methodPostProcess();
	}
	private void methodPostProcess() {
		Method[] methods = appClass.getMethods();
		for(Method method: methods) {
			//stage部分
			Annotation[] annotations = method.getAnnotations();
			LazyLoad lazyLoad = method.getAnnotation(LazyLoad.class);
			for(Annotation annotation : annotations) {
				FxMethodPostProcess<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxMethodPostProcess<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					bindListener(listener,lazyLoad,method,annotation);
				}
			}
		}
	}
	private void fieldPostProcess() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = ReflectUtils.getAllFields(appClass,FxApplication.class);
		for(Field field: fields) {
			Annotation[] annotations = field.getAnnotations();
			LazyLoad lazyLoad = field.getAnnotation(LazyLoad.class);
			for(Annotation annotation : annotations) {
				FxFieldPostProcess<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxFieldPostProcess<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					bindListener(listener,lazyLoad,field,annotation);
				}
			}
		}
		
	}

	private void bindListener(FxFieldProcess<Annotation> listener, LazyLoad lazyLoad, Field field,
			Annotation annotation) {
		try {
			if(lazyLoad != null && listener instanceof LazyLoader) {
				fxmlLoader.getNamespace().addListener(new MapChangeListener<String, Object>(){
					@SuppressWarnings("unchecked")
					@Override
					public void onChanged(Change<? extends String, ? extends Object> change) {
						if(StringUtil.equals(change.getKey(), ((LazyLoader<Annotation>)listener).id(annotation,field))){
							try {
								listener.adapter(FxApplication.this, field,annotation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}else {
				listener.adapter(this, field,annotation);
			}
		} catch (Exception e) {
			throw new RuntimeException("exception occur at process field " +field+" at "+this.appClass.getName(),e);
		}
		
	}
	private void bindListener(FxMethodProcess<Annotation> listener, LazyLoad lazyLoad, Method method,Annotation annotation) {
 		try {
			if(lazyLoad != null && listener instanceof LazyLoader) {
				fxmlLoader.getNamespace().addListener(new MapChangeListener<String, Object>(){
					@SuppressWarnings("unchecked")
					@Override
					public void onChanged(Change<? extends String, ? extends Object> change) {
						if(StringUtil.equals(change.getKey(), ((LazyLoader<Annotation>)listener).id(annotation,method))){
							try {
								listener.adapter(FxApplication.this, method,annotation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}else {
				listener.adapter(this, method,annotation);
			}
		} catch (Exception e) {
			throw new RuntimeException("exception occur at process method " +method+" at "+this.appClass.getName(),e);
		}
	}
}
