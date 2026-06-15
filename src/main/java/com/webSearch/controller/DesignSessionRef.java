package com.webSearch.controller;

/** Lightweight handle returned when a design run is started. */
public record DesignSessionRef(String id, String status) {
}
