/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at OpenObserve (https://www.openobserve.ai/).
 * Copyright 2016-Present OpenObserve, Inc.
 */

package com.openobserve.gradle.plugin.jsonschema.generator

import com.openobserve.gradle.plugin.jsonschema.Animal
import com.openobserve.gradle.plugin.jsonschema.Article
import com.openobserve.gradle.plugin.jsonschema.Bike
import com.openobserve.gradle.plugin.jsonschema.Book
import com.openobserve.gradle.plugin.jsonschema.Comment
import com.openobserve.gradle.plugin.jsonschema.Company
import com.openobserve.gradle.plugin.jsonschema.Conflict
import com.openobserve.gradle.plugin.jsonschema.Customer
import com.openobserve.gradle.plugin.jsonschema.DateTime
import com.openobserve.gradle.plugin.jsonschema.Delivery
import com.openobserve.gradle.plugin.jsonschema.Demo
import com.openobserve.gradle.plugin.jsonschema.Foo
import com.openobserve.gradle.plugin.jsonschema.Household
import com.openobserve.gradle.plugin.jsonschema.Jacket
import com.openobserve.gradle.plugin.jsonschema.Location
import com.openobserve.gradle.plugin.jsonschema.Message
import com.openobserve.gradle.plugin.jsonschema.NoOpLogger
import com.openobserve.gradle.plugin.jsonschema.Opus
import com.openobserve.gradle.plugin.jsonschema.Order
import com.openobserve.gradle.plugin.jsonschema.Paper
import com.openobserve.gradle.plugin.jsonschema.PathArrayWithInteger
import com.openobserve.gradle.plugin.jsonschema.PathArrayWithNumber
import com.openobserve.gradle.plugin.jsonschema.Person
import com.openobserve.gradle.plugin.jsonschema.Product
import com.openobserve.gradle.plugin.jsonschema.Shipping
import com.openobserve.gradle.plugin.jsonschema.Style
import com.openobserve.gradle.plugin.jsonschema.TypeDefinition
import com.openobserve.gradle.plugin.jsonschema.User
import com.openobserve.gradle.plugin.jsonschema.UserMerged
import com.openobserve.gradle.plugin.jsonschema.Version
import com.openobserve.gradle.plugin.jsonschema.Video
import com.openobserve.gradle.plugin.jsonschema.WeirdCombo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@RunWith(Parameterized::class)
class FileGeneratorTest(
    private val inputType: TypeDefinition,
    private val outputFile: String
) {

    @get:Rule
    val tempFolderRule = TemporaryFolder()

    lateinit var tempDir: File

    @Test
    fun `generates a Poko file`() {
        tempDir = tempFolderRule.newFolder()
        val clazz = FileGeneratorTest::class.java
        val outputPath = clazz.getResource("/output/$outputFile.kt").file
        val testedGenerator = FileGenerator(tempDir, "com.example.model", NoOpLogger())

        testedGenerator.generate(inputType)

        val generatedFile = Files.find(
            Paths.get(tempDir.toURI()),
            Integer.MAX_VALUE,
            { _, attrs -> attrs.isRegularFile }
        ).findFirst().get().toFile()

        val generatedContent = generatedFile.readText(Charsets.UTF_8)
        val expectedContent = File(outputPath).readText(Charsets.UTF_8)

        if (generatedContent != expectedContent) {
            val genLines = generatedContent.lines()
            val expLines = expectedContent.lines()
            for (i in 0 until minOf(genLines.size, expLines.size)) {
                if (genLines[i] != expLines[i]) {
                    System.err.println("--- GENERATED $outputFile.kt \n")
                    System.err.println(generatedContent)
                    throw AssertionError(
                        "File $outputFile generated from \n$inputType didn't match expectation:\n" +
                            "First error on line ${i + 1}:\n" +
                            "<<<<<<< EXPECTED\n" +
                            expLines[i] +
                            "\n=======\n" +
                            genLines[i] +
                            "\n>>>>>>> GENERATED\n"
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {1}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(Article, "Article"),
                arrayOf(Animal, "Animal"),
                arrayOf(Bike, "Bike"),
                arrayOf(Book, "Book"),
                arrayOf(Comment, "Comment"),
                arrayOf(Company, "Company"),
                arrayOf(Conflict, "Conflict"),
                arrayOf(Customer, "Customer"),
                arrayOf(DateTime, "DateTime"),
                arrayOf(Delivery, "Delivery"),
                arrayOf(Demo, "Demo"),
                arrayOf(Foo, "Foo"),
                arrayOf(Household, "Household"),
                arrayOf(Jacket, "Jacket"),
                arrayOf(Person, "Person"),
                arrayOf(Location, "Location"),
                arrayOf(Message, "Message"),
                arrayOf(Order, "Order"),
                arrayOf(Opus, "Opus"),
                arrayOf(Paper, "Paper"),
                arrayOf(Product, "Product"),
                arrayOf(Shipping, "Shipping"),
                arrayOf(Style, "Style"),
                arrayOf(Order, "Order"),
                arrayOf(User, "User"),
                arrayOf(UserMerged, "UserMerged"),
                arrayOf(Version, "Version"),
                arrayOf(Video, "Video"),
                arrayOf(WeirdCombo, "WeirdCombo"),
                arrayOf(PathArrayWithInteger, "PathArrayWithInteger"),
                arrayOf(PathArrayWithNumber, "PathArrayWithNumber")
            )
        }
    }
}
