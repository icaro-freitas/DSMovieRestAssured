package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String movieTitle;
	
	private Long existingMovieId;

	private Long nonExistingMovieId;
	
	
	@BeforeEach
	public void setUp() {
		movieTitle = "Rogue";
		existingMovieId = 1L;
		nonExistingMovieId = 200L;
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
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
	}
}
