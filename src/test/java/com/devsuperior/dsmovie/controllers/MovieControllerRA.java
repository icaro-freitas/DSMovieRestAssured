package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class MovieControllerRA {
	
	private String movieTitle;
	
	private Long existingMovieId;

	private Long nonExistingMovieId;
	
	private String clientUserName;

	private String clientPassword;

	private String adminUserName;

	private String adminPassword;

	private String clientToken;

	private String adminToken;

	private String invalidToken;
	
	private Map<String, Object> postMovieInstance;	
	
	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080";
		clientUserName = "alex@gmail.com";
		clientPassword = "123456";
		adminUserName = "maria@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUserName, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUserName, adminPassword);
		invalidToken = adminToken + "xpto";
		
		movieTitle = "Rogue";
		existingMovieId = 1L;
		nonExistingMovieId = 200L;
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image",
				"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");	
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given().get("/movies?page=0").then().statusCode(200).body("content.title",
				hasItems("The Witcher", "Venom: Tempo de Carnificina"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		
		given().get("/movies?title={movieTitle}", movieTitle).then().statusCode(200).body("content.id[0]", is(10))
		.body("content.title[0]", equalTo("Rogue One: Uma História Star Wars")).body("content.score[0]", is(0.0F))
		.body("content.count[0]", is(0))
		.body("content.image[0]", equalTo(
				"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/6t8ES1d12OzWyCGxBeDYLHoaDrT.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {	
		
		given().get("/movies/{id}", existingMovieId).then().statusCode(200).body("id", is(1))
				.body("title", equalTo("The Witcher"))
				.body("image", equalTo(
						"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
				.body("score", is(4.5F)).body("count", is(2));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
		given()		
		.get("/movies/{id}", nonExistingMovieId)
		.then()
		.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {	
		postMovieInstance.put("title", " ");

		JSONObject newMovie = new JSONObject(postMovieInstance);

		given().header("Content-type", "application/json").header("Authorization", "Bearer " + adminToken)
				.body(newMovie).contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/movies").then()
				.statusCode(422)
				.body("errors.message[1]", equalTo("Tamanho deve ser entre 5 e 80 caracteres"))	
				.body("errors.message[0]", equalTo("Campo requerido"));		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie= new JSONObject(postMovieInstance);

		given().header("Content-type", "application/json").header("Authorization", "Bearer " + clientToken)
				.body(newMovie).contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/movies").then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie= new JSONObject(postMovieInstance);

		given().header("Content-type", "application/json").header("Authorization", "Bearer " + invalidToken)
				.body(newMovie).contentType(ContentType.JSON).accept(ContentType.JSON).when().post("/movies").then()
				.statusCode(401);
	}
}
