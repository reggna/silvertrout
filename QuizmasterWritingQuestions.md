# Writing questions for Quizmaster #



**TODO: talk about more about that there really are no different types of questions. Give more examples of how it will play out in the channel.**

**TODO: More about naming files / categories and file structure**

Quizmaster uses an [XML](http://en.wikipedia.org/wiki/XML) format which gives you the posibility to write many types of questions from the simple questions with one answer to questions with multiple answers and mutiple hints.

A set of question is contained inside an file named `subcategory.xml` which should be placed in a folder `category` (inside resources/silvertrout/plugins/quizmaster/Questions) and should be in the following format:

```
<questions 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:noNamespaceSchemaLocation="questions.xsd"
        category="Category" subcategory="Subcategory">
  ....

</questions>

```

Each question is written in a question tag:

```
  <question>
    ...
  </question>

```

# Types of questions #

Even if there are different types of questions they are all built up using the same building blocks. All questions are written inside the questions tag and needs to have at least a question line and one answer.

## One question, one answer ##

This is equal to the legacy format described at the end of this article.

```
  <question>
    <line>What is the capital of Sweden?</line>
    <answers>
      <answer>Stockholm</answer>
    </answers>
  </question>
```

This will play out something like this:

<pre>
*[Geography - Capitals] What is the capital of Sweden?*<br>
.........<br>
.to......<br>
.to...o..<br>
.toc..ol.<br>
.toc..olm<br>
The right answer was "Stockholm". No one manage to guess it correctly.<br>
</pre>

## Multiple answers ##

A question can have multiple answers. This gives the posibility to do a couple of more question types. By default, when specifying more then one answer, just one of the answers are required to answer the question.

```
  <question>
    <line>Name one colour found in the Italian flag.</line>
    <answers>
      <answer>Red</answer>
      <answer>White</answer>
      <answer>Green</answer>
    </answers>
  </question>
```

Answers can have the following attributes:

  * Score    (default: 5)
  * Required (default: false)

Besides this one can also specify globally in the answers tag:

  * Required answers  (default: 1)

Using this, we can construct questions like this:

```
  <question>
    <line>Name the colour of the middle ribbon and at least 
          one more colour found in the Italian flag.</line>
    <answers required="2">
      <answer score="2">Red</answer>
      <answer required="true">White</answer>
      <answer score="8">Green</answer>
    </answers>
  </question>
```

To answer this question correctly one need to name at least two of the three colours in the Italian flag where one of the colours must be White. If one answers Red and Green, one would get 7 points. If one would answer White and Green one would get 13 points. Answering Red, White and Green would give a total of 15 points.

## Multiple hints ##

If you want to go beyond the simple hint line that Quizmaster construct for you and make your own there are a few choice for you. Basically you add a `hints` tag after `line` in your `question` tag.

Making our own hint line:

```
  <question>
    <line>What is the colours of the Italian flag?</line>
    <hints>
      <line>Red, Green and White</line>
    </hints>
    <answers required="3">
      <answer>Red</answer>
      <answer>White</answer>
      <answer>Green</answer>
    </answers>
  </question>  
```

One can also create additional hints:

```
  <question>
    <line>What is the colours of the Italian flag?</line>
    <hints>
      <hint>The Italian flag has the same colours as the Bulgarian flag.</hint>
      <hint>The Italian flag is a tricolour</hint>
      <hint>The colours are said to represent country's plains and the hills, 
            the snow-capped Alps and the blood spilt in the Wars of Italian 
            Independence.</hint>
    </hints>
    <answers required="3">
      <answer>Red</answer>
      <answer>White</answer>
      <answer>Green</answer>
    </answers>
  </question>  
```

**TODO: talk about score decrease.**

# Tips #

There are many things that you must think of when writing questions. Try to be specific and make sure your question can only be interpreted in one way.

Things _not_ to do:

  * **Do not write questions with incredible long answers.** If you write a question with an answer that is over 30 characters long, people probably won't answer it. (e.g. List all dwarves in Snow White)
  * **Do not write too specific answers.** This include answers such as ("About 7 inches", "About five or six miles" or "It is the house next to the White House")
  * **Try to avoid writing questions that will quickly become obsolete.** (e.g. "Who won 4 gold medals at this years Olympic games?" or "...")

Things to do:
Write fun questions

**TODO: add lots of stuff here**

# Legacy format #

Before converting to the more powerful XML format Quizmaster used a simpler text based format. This can still be used to construct questions. However these text files must be converted to XML using the converter program.

**TODO: talk about the converter program**

Format of the legacy question files:

```
Category - Subcategory

Question?
Answer

Quesiton
Answer

```