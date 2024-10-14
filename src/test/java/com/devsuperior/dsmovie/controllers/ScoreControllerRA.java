package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class ScoreControllerRA {

	private Long existingMovieId;

	private Long nonExistingMovieId;

	private String clientUserName;

	private String clientPassword;

	private String adminUserName;

	private String adminPassword;

	private String clientToken;

	private String adminToken;	

	private Map<String, Object> saveScoreInstance;

	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080";
		clientUserName = "alex@gmail.com";
		clientPassword = "123456";
		adminUserName = "maria@gmail.com";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUserName, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUserName, adminPassword);		

		existingMovieId = 1L;
		nonExistingMovieId = 200L;

		saveScoreInstance = new HashMap<>();
		saveScoreInstance.put("movieId", existingMovieId);
		saveScoreInstance.put("score", 4);

	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		saveScoreInstance.put("movieId", nonExistingMovieId);

		JSONObject newScore = new JSONObject(saveScoreInstance);

		given().header("Content-type", "application/json").header("Authorization", "Bearer " + clientToken)
				.body(newScore).contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.put("/scores").then().statusCode(404);
	}

	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {

		saveScoreInstance.put("movieId", null);

		JSONObject newScore = new JSONObject(saveScoreInstance);

		given().header("Content-type", "application/json").header("Authorization", "Bearer " + adminToken)
				.body(newScore).contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.put("/scores").then().statusCode(422)
				.body("errors.message[0]", equalTo("Campo requerido"));

	}

	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		
		saveScoreInstance.put("score", -2);

		JSONObject newScore = new JSONObject(saveScoreInstance);

		given().header("Content-type", "application/json").header("Authorization", "Bearer " + adminToken)
				.body(newScore).contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.put("/scores").then().statusCode(422)
				.body("errors.message[0]", equalTo("Valor mínimo 0"));		
		
	}
}
