package com.compiler.ast.types;

public sealed interface UserType extends TypeNode permits ArrayType, RecordType {
}
