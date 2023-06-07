package com.example.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

//@SpringBootApplication
//@OpenAPIDefinition
public class CafeManagementSystemApplication {

	// nums = [2, 7, 11, 15], target = 9
	public static int[] twoSum(int[] nums, int target) {
		Map<Integer, Integer> map = new HashMap<>();

		for (int i = 0; i < nums.length; i++) {
			int result = target - nums[i];

			if (map.containsKey(result)) {
				return new int[] { map.get(result), i };
			}
			map.put(nums[i], i);

		}

		throw new IllegalArgumentException("No two sum solution");
	}

	public static void main(String[] args) {
//		SpringApplication.run(CafeManagementSystemApplication.class, args);

		int[] nums = new int[] { 2, 7, 11, 15 };

		System.out.println(Arrays.toString(twoSum(nums, 9)));

	}

}
