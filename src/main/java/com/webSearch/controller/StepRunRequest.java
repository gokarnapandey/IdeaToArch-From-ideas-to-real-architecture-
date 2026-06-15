package com.webSearch.controller;

/** Optional body for running a step: user feedback to fold into the agent's prompt. */
public record StepRunRequest(String feedback) {
}
