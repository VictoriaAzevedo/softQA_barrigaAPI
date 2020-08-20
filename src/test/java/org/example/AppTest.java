package org.example;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class AppTest 
{

    private String token;
    private String url;

    @Before
    public void configInicial()
    {
        url = "http://barrigarest.wcaquino.me";
        token = given().
                contentType(ContentType.JSON).
                body("{\n" +
                        "\t\"email\": \"azevedomvictoria@gmail.com\",\n" +
                        "\t\"senha\": \"jdmt@556\"\n" +
                        "}").
                when().
                post(url + "/signin").
                then().
                log().all().
                statusCode(SC_OK).
                body("id", equalTo(11241)).
                body("nome", equalToIgnoringCase("victoria")).
                body("token", notNullValue()).extract().path("token");
    }

    @Test
    public void validarAcessoAPI()
    {
        given().
        when().
            get(url + "/contas").
        then().
                log().all().
            statusCode(SC_UNAUTHORIZED);
    }

    @Test
    public void validarAcessoLogin()
    {
        given().
                contentType(ContentType.JSON).
            body("{\n" +
                    "\t\"email\": \"azevedomvictoria@gmail.com\",\n" +
                    "\t\"senha\": \"jdmt@556\"\n" +
                    "}").
        when().
            post(url + "/signin").
        then().
            log().all().
            statusCode(SC_OK).
            body("id", equalTo(11241)).
            body("nome", equalToIgnoringCase("victoria")).
            body("token", notNullValue());
    }

    @Test
    public void incluirContaSucesso(){
        given().
                header("Authorization", "JWT " + token).
                contentType(ContentType.JSON).
                body("\n" +
                        "{\n" +
                        "\t\"nome\": \"Conta2\"\n" +
                        "}").
        when().
                post(url + "/contas").
        then().
                log().all().
                statusCode(SC_CREATED).
                body("id", notNullValue());
    }

    @Test
    public void alterarNomeConta(){
        given().
            header("Authorization", "JWT " + token).
            contentType(ContentType.JSON).
            body("\n" +
                    "{\n" +
                    "\t\"nome\": \"ContaNova\"\n" +
                    "}").
        when().
            put(url + "/contas/242890").
        then().
            log().all().statusCode(SC_OK).
            body("nome", is("ContaNova"));
    }

    @Test
    public void naoAdicionarNomeContaIgual(){
        given().
                header("Authorization", "JWT " + token).
                contentType(ContentType.JSON).
                body("\n" +
                        "{\n" +
                        "\t\"nome\": \"ContaNova\"\n" +
                        "}").
                when().
                post(url + "/contas").
                then().
                log().all().
                body("error", is("Já existe uma conta com esse nome!"));
    }

    @Test
    public void inserirMovimentacaoSucesso(){
        given().
                header("Authorization", "JWT " + token).
                contentType(ContentType.JSON).
                body("{\n" +
                        "\t\"conta_id\": ,\n" +
                        "\t\"descricao\": \"movimentacao1\",\n" +
                        "\t\"envolvido\": \"teste\",\n" +
                        "\t\"tipo\": \"REC\",\n" +
                        "\t\"data_transacao\": \"18/08/2019\",\n" +
                        "\t\"data_pagamento\": \"20/08/2020\",\n" +
                        "\t\"valor\": 1000,\n" +
                        "\t\"status\": true\n" +
                        "}").
        when().
                post("/transacoes").
        then().
                log().all().
                statusCode(SC_CREATED);
    }


}
