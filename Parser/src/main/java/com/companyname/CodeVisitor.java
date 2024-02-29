package com.companyname;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class CodeVisitor extends VoidVisitorAdapter<List<String>> {

    @Override
    public void visit(ClassOrInterfaceDeclaration cls, List<String> report) {
        // Các package trong dự án phải theo mẫu: com.companyname.* (*:tên bất kỳ)
        if (!cls.getFullyQualifiedName().get().startsWith("com.companyname")) {
            report.add("Package name does not follow naming convention com.companyname.*: " + cls.getFullyQualifiedName().get());
        }


        // Các class phải có tên là một danh từ hoặc cụm danh ngữ và phải bắt đầu bằng chữ hoa.
        if (!cls.getNameAsString().matches("[A-Z][a-zA-Z]*")) {
            report.add("Class name does not follow naming convention: " + cls.getNameAsString());
        }

        // Mỗi lớp phải có một comment mô tả cho lớp. Trong comment đó phải có ngày tạo(created-date) và author.
        if (!cls.getComment().isPresent()) {
            report.add("Class " + cls.getNameAsString() + " is missing a comment");
        } else {
            Comment comment = cls.getComment().get();
            if (!comment.getContent().contains("created-date") || !comment.getContent().contains("author")) {
                report.add("Class " + cls.getNameAsString() + " comment is missing required information");
            }
        }
        //Các fields trong các class phải là danh từ hoặc cụm danh ngữ và phải bắt đầu bằng một chữ thường.
        if (cls.getFields().size() > 0) {
            cls.getFields().forEach(field -> {
                field.getVariables().forEach(variable -> {
                    if (!variable.getNameAsString().matches("[a-z][a-zA-Z]*")) {
                        report.add("Field name does not follow naming convention: " + variable.getNameAsString());
                    }
                });
            });
        }
        //Tất cả các hằng số phải là chữ viết hoa và phải nằm trong một interface.
        if(cls.isInterface()){
            cls.getFields().forEach(field -> {
                field.getVariables().forEach(variable -> {
                    if (!variable.getNameAsString().matches("[A-Z][A-Z_]*")) {
                        report.add("Constant name does not follow naming convention: " + variable.getNameAsString());
                    }
                });
            });
        }
        // nếu không phải là interface thì không được chứa hằng số
        if(!cls.isInterface()){
            cls.getFields().forEach(field -> {
                field.getVariables().forEach(variable -> {
                    if (variable.getNameAsString().matches("[A-Z][A-Z_]*")) {
                        report.add("Class " + cls.getNameAsString() + " contains constant");
                    }
                });
            });
        }

        //Tên method phải bắt đầu bằng một động từ và phải là chữ thường
        cls.getMethods().forEach(method -> {
            if (!method.getNameAsString().matches("[a-z][a-zA-Z]*")) {
                report.add("Method name does not follow naming convention: " + method.getNameAsString());
            }
        });
        // Mỗi method phải có một ghi chú mô tả cho công việc của method trừ phương thức
        // default constructor, accessors/mutators, hashCode, equals, toString.
        cls.getMethods().forEach(method -> {
            if (!method.getNameAsString().equals("hashCode") && !method.getNameAsString().equals("equals") && !method.getNameAsString().equals("toString")) {
                if (!method.getComment().isPresent()) {
                    report.add("Method " + method.getNameAsString() + " is missing a comment");
                }
            }
        });


        super.visit(cls, report);
    }

}

