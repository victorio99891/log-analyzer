package com.example.core_modules.config;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
@Getter
public class ConfigurationModel {
    private String logDelimiterPattern;
    private String historyFileName;
    private List<String> regexFilterList = new ArrayList<>();
}
