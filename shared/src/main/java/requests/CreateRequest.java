package service.requests;

import model.Game;

public record CreateRequest(String authToken, Game gameName) {
}