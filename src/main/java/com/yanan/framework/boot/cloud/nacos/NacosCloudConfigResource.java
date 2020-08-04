package com.yanan.framework.boot.cloud.nacos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.yanan.utils.resource.Resource;

/**
 * nacos 云资源
 * @author yanan
 */
public class NacosCloudConfigResource implements Resource{
	private String groupId;
	private String dataId;
	private volatile InputStream inputStream;
	private String content;
	private NacosConfigRuntime nacosConfigRuntime;
	public NacosCloudConfigResource(String groupId, String dataId, String content,NacosConfigRuntime nacosConfigRuntime) {
		super();
		this.groupId = groupId;
		this.dataId = dataId;
		this.content = content;
		this.nacosConfigRuntime = nacosConfigRuntime;
	}
	@Override
	public String getPath() {
		return groupId;
	}
	@Override
	public boolean isDirect() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long lastModified() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long size() throws IOException {
		return getInputStream().available();
	}

	@Override
	public List<? extends Resource> listResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if(inputStream == null) {
			synchronized (this) {
				if(inputStream == null) {
					if(content == null)
						throw new IOException("the resource "+groupId+"\\"+dataId+" is not exists");
					this.inputStream = new ByteArrayInputStream(content.getBytes());
				}
			}
		}
		return inputStream;
	}

	@Override
	public String getName() {
		return dataId;
	}
	public String getContent() {
		return content;
	}
	public NacosConfigRuntime getNacosConfigRuntime() {
		return nacosConfigRuntime;
	}

}