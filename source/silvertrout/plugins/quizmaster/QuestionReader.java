/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav "Gussoh" Sohtell
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package silvertrout.plugins.quizmaster;

import java.net.URL;

import java.io.File;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import silvertrout.commons.*;
/**
 * QuestionReader reads in question files for the Quizmaster plugin.
 * 
 */
public class QuestionReader {
    /**
     * Question handler to convert xml-questions to java-question.
     */
	private static class QuestionHandler extends DefaultHandler {
		
		private String              category  = null;
		private Question            question  = null;
		private ArrayList<String>   tags      = new ArrayList<String>();
		private ArrayList<Question> questions = new ArrayList<Question>();
		private Locator             locator   = null;
        private String              file      = null;
		public QuestionHandler(String file) {
			super();
            this.file = file;
		}
		
		public Collection<Question> getQuestions() {
			return questions;
		}
		
		private String currentTag() {
			if(tags.size() == 0) {
				return null;
			} else {
				return tags.get(tags.size() - 1);
			}
		}
		
		private String previousTag() {
			if(tags.size() < 2) {
				return null;
			} else {
				return tags.get(tags.size() - 2);
			}
		}
        @Override
        public void setDocumentLocator(Locator locator)
        {
            this.locator = locator;
        }
        @Override
		public void startElement (String uri, String name, String qName, Attributes atts)
		{
			tags.add(qName);
			
			if(qName.equals("questions")) {
				category = atts.getValue("category") + " - " + atts.getValue("subcategory");
			} else if(qName.equals("question")) {
				question = new Question();
				question.category = category;
                question.file     = file;
                question.row      = locator.getLineNumber();
			} else if(qName.equals("line")) {
				if(previousTag().equals("hints")) {
					if(atts.getValue("hints") != null)question.hintCount = Integer.parseInt(atts.getValue("hints")) + question.hints.size(); 
				}
			} else if(qName.equals("answers")) {
				if(atts.getValue("attempts") != null)question.attempts = Integer.parseInt(atts.getValue("attempts"));
				if(atts.getValue("required") != null)question.required = Integer.parseInt(atts.getValue("required"));
			} else if(qName.equals("answer")) {
                //System.out.println(qName + " = " + atts.getValue("required"));
				Question.Answer newAns = question.new Answer();
				if(atts.getValue("required") != null
                        && (atts.getValue("required").equals("1")
                        || atts.getValue("required").equals("true")))newAns.required = true;
				if(atts.getValue("score") != null)newAns.score = Integer.parseInt(atts.getValue("score"));
				question.answers.add(newAns);
			} else if(qName.equals("hints")) {
			
			} else if(qName.equals("hint")) {
				Question.Hint newHint = question.new Hint();
				if(atts.getValue("score-decrease") != null)newHint.scoredec = Integer.parseInt(atts.getValue("score-decrease"));
				question.hints.add(newHint);
			}
		}
        @Override
		public void characters(char[] ch, int start, int length) {
			String data = EscapeUtils.normalizeSpaces(
			        new String(ch, start, length));
			if(currentTag().equals("line")) {
				if(previousTag().equals("question")) {
					question.questionLine += data;
				} else if(previousTag().equals("hints")) {
				    if(question.hintLine != null) {
    					question.hintLine += data;
					} else {
						question.hintLine = data;
					}
				}
			} else if(currentTag().equals("hint")) {
				question.hints.get(question.hints.size() - 1).hint += data;
			} else if(currentTag().equals("answer")) {
				question.answers.get(question.answers.size() - 1).answer += data;
			}
		}


        @Override
		public void endElement (String uri, String name, String qName)
		{
			tags.remove(tags.size() - 1);
			if(qName.equals("question")) {
				// TODO: fix stuff
				questions.add(question);
			}
		}
	}
    
	
	public static Collection<Question> load(File file) {
		
		try {    
			// Set up schema:
			String        language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory factory  = SchemaFactory.newInstance(language);
            String        sPath    = "questions.xsd";
            URL           sURL     = QuestionHandler.class.getResource(sPath);
			Schema        schema   = factory.newSchema(sURL);
			// Validate:
			Validator validator = schema.newValidator();
			SAXSource source = new SAXSource(new InputSource(new java.io.FileInputStream(file)));
			validator.validate(source);
			// Parse:
			SAXParserFactory sf = SAXParserFactory.newInstance();
			sf.setNamespaceAware(true); 
			sf.setValidating(true);        
			sf.setSchema(schema);
			SAXParser       sp = sf.newSAXParser();
			QuestionHandler qh = new QuestionHandler(file.toString());
			sp.parse(file, qh);
			
			return qh.getQuestions();
		
		} catch (SAXParseException e) {
            System.err.println("Error parsing file " + file);
            System.err.println("At line " + e.getLineNumber() + ", column " + e.getColumnNumber());
			System.err.println(e.getMessage());
            System.err.println("==============================");
            e.printStackTrace();
        } catch(SAXException e) {
            System.err.println("Error parsing file " + file);
			System.err.println(e.getMessage());
            System.err.println("==============================");
            e.printStackTrace();
		} catch(java.io.FileNotFoundException e) {
			System.err.println(e.getMessage());
            e.printStackTrace();
		} catch(java.io.IOException e) {
			System.err.println(e.getMessage());
            e.printStackTrace();
		} catch(javax.xml.parsers.ParserConfigurationException e) {
			System.err.println(e.getMessage());
            e.printStackTrace();
		}
        return null;
	}

    private QuestionReader() {
    }

}
