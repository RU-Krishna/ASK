package com.features.ask.ui

data class RandomQuestions(
    val question: String = "",
    val id: Int = 0
)
val randomQuestions = listOf(
    RandomQuestions(
        question = "Write a rap verse about the potential of artificial intelligence to change the world.",
        id = 0
    ),
    RandomQuestions(
        question = "Come up with a code of ethics for AI development and implementation",
        id = 1
    ),
    RandomQuestions(
        question = "If you could have any superpower, what would it be and how would you use it for good?",
        id = 2
    ),
    RandomQuestions(
        question = "If you could learn a new skill or ability, what would it be and how would you use it?",
        id = 3
    ),
    RandomQuestions(
        question =  "How do you think the way humans interact with information will change in the next 50 years?",
        id = 4
    ),
    RandomQuestions(
        question =  "What is the most interesting or surprising thing you have learned about the world through the information you have access to?",
        id = 5
    ),
    RandomQuestions(
        question = "What do you think is the biggest challenge facing humanity today, and how can large language models like me be part of the solution?",
        id = 6
    ),
    RandomQuestions(
        question =   "If you could access and process information from the real world in real-time, how would it change the way you answer questions?",
        id = 7
    )
)


