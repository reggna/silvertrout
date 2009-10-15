import java.io.File;

import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

import java.util.LinkedList;
import java.util.Collections;



class Converter {

    static private class Question {
        public String question;
        public String answer;
        public String category;
    }

    static void convertQuestions(File f) {
    
        LinkedList<Question> questions = new LinkedList<Question>();
        
        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));
            String category = fr.readLine();
            fr.readLine();
            while(true) {
                Question q = new Question();
                q.category = category;
                q.question = fr.readLine();
                q.answer   = fr.readLine();
                fr.readLine();
                if(q.question == null || q.answer == null)break;
                questions.add(q);
            }
            fr.close();
            
            System.out.println(category);
            String[] cat        = category.split("-", 2);
            String mainCategory = cat[0].trim();
            String subCategory  = cat[1].trim();
            
            BufferedWriter fw = new BufferedWriter(new FileWriter(f)); //new OutputStreamWriter(System.out)
            fw.write("<questions \n" +
                    "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
                    "        xsi:noNamespaceSchemaLocation=\"../../questions.xsd\" \n" +
                    "        category=\"" + mainCategory + "\" subcategory=\"" + subCategory + "\">\n\n");
            for(Question q: questions) {
                fw.write("  <question>\n");
                fw.write("    <line>" + q.question + "</line>\n");
                fw.write("    <answers>\n");
                fw.write("      <answer>" + q.answer + "</answer>\n");
                fw.write("    </answers>\n");                            
                fw.write("  </question>\n\n");
            }
            fw.write("</questions>\n");
            fw.close();
            System.out.println("..");
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
        System.out.println("...done (" + questions.size() + ")");
    }



    public static void main(String[] args) {
        try {
            File f = new File(args[0]);
            System.out.println("Converting file " + f.getPath() + " - " + f.getName());
            convertQuestions(f);
        } catch(Exception e) {
            System.out.println("An error occured:");
            e.printStackTrace();
        }
    }
}
