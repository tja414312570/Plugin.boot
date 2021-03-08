package com.yanan.plugs.mvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import com.google.gson.Gson;
import com.yanan.billing.constant.ResponseCode;
import com.yanan.billing.model.response.BaseAppResult;
import com.yanan.framework.plugin.annotations.Register;
import com.yanan.framework.webmvc.RequestContext;
import com.yanan.framework.webmvc.ServletBean;
import com.yanan.framework.webmvc.annotations.restful.ParameterType;
import com.yanan.framework.webmvc.parameter.annotations.RequestBody;
import com.yanan.framework.webmvc.parameter.annotations.RequestParam;
import com.yanan.framework.webmvc.response.ServletExceptionHandler;
import com.yanan.framework.webmvc.validator.ParameterVerificationFailed;

/**
 * 参数验证响应处理
 * @author yanan
 *
 */
@Register
public class DefaultParameterVerifyFailedHandler implements ServletExceptionHandler {

	@Override
	public void exception(Throwable e, HttpServletRequest request, HttpServletResponse response,
			ServletBean servletBean) throws ServletException, IOException {
			e.printStackTrace();
			while(e.getCause()!=null){
				e = e.getCause();
			}
			if(e.getClass().equals(ParameterVerificationFailed.class)){
				ParameterVerificationFailed p = (ParameterVerificationFailed) e;
				response.setContentType("application/json;charset=utf-8");
				response.setStatus(200);//HttpServletResponse.SC_BAD_REQUEST);
				if (!response.isCommitted()) {
					Map<Object, ConstraintViolation<?>> map = p.getValidResult();
					for(java.util.Map.Entry<Object, ConstraintViolation<?>> entry : map.entrySet()){
						BaseAppResult baseAppResult = new BaseAppResult();
						baseAppResult.setCode(ResponseCode.Parameter_Verify_Failed);
						baseAppResult.setMessage(entry.getValue().getMessage());
						String name = null ;
						if(entry.getKey().getClass().equals(Field.class)){
							name = ((Field)entry.getKey()).getName();
						}else{
							Parameter param = (Parameter) entry.getKey();
							List<Annotation> annos = RequestContext.getCurrentThreadContext()
									.getServletBean().getParameterAnnotation(param, ParameterType.class);
							if(annos != null)
							for(Annotation anno : annos)
								name = getParameterName(anno);
							if(name == null)
								name = param.getName();
						}
						baseAppResult.setAttach(name);
						response.getWriter().write(new Gson().toJson(baseAppResult));
						response.getWriter().flush();
						response.getWriter().close();
						break;
					}
			}
			}
	}

	private String getParameterName(Annotation anno) {
		Class<?> annoClass = anno.annotationType();
		
		if(annoClass.equals(RequestParam.class)){
			RequestParam requestParam = (RequestParam) anno;
			if(!requestParam.value().equals("")){
				return requestParam.value();
			}
		}

		if(annoClass.equals(RequestBody.class)){
			RequestBody requestParam = (RequestBody) anno;
			if(!requestParam.value().equals("")){
				return requestParam.value();
			}
		}
		return null;
	}

}