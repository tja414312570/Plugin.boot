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
import com.yanan.framework.fx.listener.field.FxFieldListener;
import com.yanan.framework.fx.listener.method.FxMethodListener;
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
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
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
	private void bind() throws Exception {
		appClass = getClass();
		if(PlugsFactory.isProxy(this)) {
			PlugsHandler plugsHandler = PlugsFactory.getPluginsHandler(this);
			appClass = plugsHandler.getRegisterDefinition().getRegisterClass();
		}
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
			if(field.getAnnotation(Bind.class) != null) {
				ReflectUtils.setFieldValue(field, this, controller);
			}
			if(field.getAnnotation(View.class) != null) {
				ReflectUtils.setFieldValue(field, this, root);
			}
			if(field.getAnnotation(BindStage.class) != null) {
				ReflectUtils.setFieldValue(field, this, primaryStage);
			}
			FindViewById findViewById = field.getAnnotation(FindViewById.class);
			if(findViewById != null) {
				String name = field.getName();
				if(StringUtil.isNotEmpty(findViewById.value())) {
					name = findViewById.value();
				}
				Object view = findViewById(name);
				ReflectUtils.setFieldValue(field, this, view);
			}
			FindView findView = field.getAnnotation(FindView.class);
			if(findView != null) {
				String name = field.getName();
				if(StringUtil.isNotEmpty(findView.value())) {
					name = findView.value();
				}
				Object view = root.lookup(name);
				ReflectUtils.setFieldValue(field, this, view);
			}
			Annotation[] annotations = field.getAnnotations();
			for(Annotation annotation : annotations) {
				FxFieldListener<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxFieldListener<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					try {
						listener.adapter(this, field,annotation);
					} catch (Exception e) {
						throw new RuntimeException("exception occur at process field " +field+" at "+this.appClass.getName(),e);
					}
				}
			}
			
		}
	}
	public <T> T findViewByField(Field field) {
		FindViewById findViewById = field.getAnnotation(FindViewById.class);
		Assert.isNotNull(findViewById);
		String name = field.getName();
		if(StringUtil.isNotEmpty(findViewById.value())) {
			name = findViewById.value();
		}
		return findViewById(name);
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
			//stage部分
			Annotation[] annotations = method.getAnnotations();
			for(Annotation annotation : annotations) {
				FxMethodListener<Annotation> listener = PlugsFactory.
						getPluginsInstanceByAttributeStrictAllowNull(new TypeToken<FxMethodListener<Annotation>>() {}.getTypeClass(),
								annotation.annotationType().getSimpleName());
				if(listener != null) {
					try {
						listener.adapter(this, method,annotation);
					} catch (Exception e) {
						throw new RuntimeException("exception occur at process method " +method+" at "+this.appClass.getName(),e);
					}
				}
			}
		}
	}

 	public void callDrawUi(Runnable execute) {
 		 Platform.runLater(execute);
 	}
 	
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		bind();
		Scene scence = new Scene(root);
		this.startApp(stage);
		Assert.isNotNull(scence);
		stage.setScene(scence);
		stage.show();
	}
}
