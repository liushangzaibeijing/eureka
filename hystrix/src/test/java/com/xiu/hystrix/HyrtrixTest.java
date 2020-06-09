package com.xiu.hystrix;


import java.util.List;
import com.xiu.hystrix.hystrixtest.CommandHelloWorld;
import com.xiu.hystrix.service.HystrixService;
import com.xiu.hystrix.service.UserService;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HyrtrixTest {

	/**
	 * Hystrix Commond 的同步调用
	 */
	@Test
	public void testCommondSyn() {
		CommandHelloWorld commandHelloWorld = new CommandHelloWorld();

		String result = commandHelloWorld.execute();

		System.out.println(result);

	}

	/**
	 * Hystrix Commond 的异步调用
	 */
	@Test
	public void testCommondAsyn() throws ExecutionException, InterruptedException {
		CommandHelloWorld commandHelloWorld = new CommandHelloWorld();

		Future<String> future = commandHelloWorld.queue();
		future.isDone();


		String result = future.get();
		System.out.println(result);

	}


	/**
	 * Hystrix Commond 的同步调用
	 */
	@Test
	public void testHystrixCommand() {
		UserService userService = new UserService();

		HystrixService hystrix = new HystrixService(userService);

		List<String> result = hystrix.execute();
		for (String r: result) {
			System.out.println(r);
		}
	}

}
