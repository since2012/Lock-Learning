package org.mark.demo.threadlocal;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @FileName SessionLocalHandler
 * @Description TODO
 * @Author markt
 * @Date 2020-06-09
 * @Version 1.0
 */
@Slf4j
public class SessionLocalHandler {

	public static ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();

	@Data
	public static class Session {
		private String id;
		private String user;
		private String status;
	}

	public void createSession() {
		threadLocal.set(new Session());
	}

	public String getUser() {
		return threadLocal.get().getUser();
	}

	public String getStatus() {
		return threadLocal.get().getStatus();
	}

	public void setStatus(String status) {
		threadLocal.get().setStatus(status);
	}

	public static void main(String[] args) {
		new Thread(() -> {
			SessionLocalHandler handler = new SessionLocalHandler();
			handler.createSession();
			log.debug("{}", handler.getStatus());
			handler.setStatus("close");
			log.debug("{}", handler.getStatus());
		}).start();
	}
}