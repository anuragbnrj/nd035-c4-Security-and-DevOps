package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModifyCartRequest {
	
	@JsonProperty
	private String username;
	
	@JsonProperty
	private Long itemId;
	
	@JsonProperty
	private int quantity;

}
