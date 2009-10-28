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

import java.util.ArrayList;

/**
 * Question for the Quizmaster plugin.
 *
 */
public class Question {
	// Location information (for report / debug)
    String file         = null;
	int    row          = -1;
	// Category
	String category     = "";
    // Question and hint        
    String questionLine = "";
    String hintLine     = "";
    // Max attempts 
    int attempts        = 100;
	// Required amount of answers
    int required        = 1;
    // Number of total hints
    int hintCount       = 7;
    
	// Hint struct
    class Hint {
        String hint      = "";
        int    scoredec  = 1; //?
    }
    // Hint collection
	ArrayList<Hint>   hints   = new ArrayList<Hint>();
	
	// Answer struct
    class Answer {
        String  answer   = "";
        int     score    = 5;
        boolean required = false;
    }
	// Answer collection
    ArrayList<Answer> answers = new ArrayList<Answer>();

}
