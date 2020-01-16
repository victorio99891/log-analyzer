package com.example;


import com.example.cli.RunnerCLI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        log.info("Runner initialized!");
        RunnerCLI.getInstance().run(args);
    }
}
