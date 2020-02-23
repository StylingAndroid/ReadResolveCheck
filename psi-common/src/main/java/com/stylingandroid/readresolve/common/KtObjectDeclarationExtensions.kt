package com.stylingandroid.readresolve.common

import java.io.Serializable
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.AbbreviatedType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

val KotlinType.fqName: FqName?
    get() = when (this) {
        is AbbreviatedType -> abbreviation.fqName
        else -> constructor.declarationDescriptor?.fqNameOrNull()
    }

fun KtObjectDeclaration.isSerializable(bindingContext: BindingContext): Boolean {
    return findClassDescriptor(bindingContext).defaultType.supertypes().any { type ->
        type.fqName?.asString() == Serializable::class.java.canonicalName
    }
}

fun KtObjectDeclaration.hasReadResolve(): Boolean {
    return body?.functions?.any { namedFunction ->
        namedFunction.name == "readResolve" &&
                namedFunction.typeReference?.typeElement?.text == "Any" &&
                namedFunction.valueParameterList?.parameters?.isEmpty() ?: true
    } ?: false
}
