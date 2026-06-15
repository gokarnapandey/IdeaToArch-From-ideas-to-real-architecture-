package com.webSearch.agent.dto;

import java.util.List;

/** Output of the Requirement agent: the idea turned into engineering-ready requirements. */
public record Requirements(
        List<String> functional,
        List<String> nonFunctional,
        List<String> assumptions,
        List<String> implicit,
        List<String> openQuestions,
        List<String> outOfScope
) {}
