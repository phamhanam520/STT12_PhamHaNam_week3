package com.companyname;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String PROJECT_PATH = "C:\\Users\\phgss\\OneDrive\\Máy tính\\Kien truc\\workspace\\STT12_PhamHaNam_week3\\CheckProject";
    public static void main(String[] args) {
        List<String> report = new ArrayList<>();
        JavaParser javaParser = new JavaParser();
        try {
            Files.walk(Path.of(PROJECT_PATH))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            ParseResult<CompilationUnit> parseResult = javaParser.parse(path.toFile());
                            if (parseResult.isSuccessful()) {
                                CompilationUnit cu = parseResult.getResult().get();
                                if (cu != null) {
                                    new CodeVisitor().visit(cu, report);
                                }
                            } else {
                                System.out.println("Failed to parse file: " + path);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("somthing wrong");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print report
        report.forEach(System.out::println);
    }
}