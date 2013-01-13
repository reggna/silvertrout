/*   _______ __ __                    _______                    __   
 *  |     __|__|  |.--.--.-----.----.|_     _|.----.-----.--.--.|  |_ 
 *  |__     |  |  ||  |  |  -__|   _|  |   |  |   _|  _  |  |  ||   _|
 *  |_______|__|__| \___/|_____|__|    |___|  |__| |_____|_____||____|
 * 
 *  Copyright 2008 - Gustav Tiger, Henrik Steen and Gustav"Gussoh" Sohtell
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
package silvertrout.commons;

import java.util.HashMap;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 **
 */
public class EscapeUtils {

    private static HashMap<String, String> xmlEnts;
    private static HashMap<String, String> htmlEnts;


    static {

        // Complete list of XML entities
        xmlEnts = new HashMap<String, String>();
        xmlEnts.put("quot",      "\"");
        xmlEnts.put("amp",       "\u0026");
        xmlEnts.put("apos",      "\u0027");
        xmlEnts.put("lt",        "\u003c");
        xmlEnts.put("gt",        "\u003e");

        // Complete list of HTML entities.
        //
        // Includes all entities from
        // http://www.w3.org/TR/REC-html40/sgml/entities.html
        htmlEnts = new HashMap<String, String>();
        
        // ISO 8859-1 (Latin-1) characters
        htmlEnts.put("nbsp",     "\u00A0");
        htmlEnts.put("iexcl",    "\u00A1");
        htmlEnts.put("cent",     "\u00A2");
        htmlEnts.put("pound",    "\u00A3");
        htmlEnts.put("curren",   "\u00A4");
        htmlEnts.put("yen",      "\u00A5");
        htmlEnts.put("brvbar",   "\u00A6");
        htmlEnts.put("sect",     "\u00A7");
        htmlEnts.put("uml",      "\u00A8");
        htmlEnts.put("copy",     "\u00A9");
        htmlEnts.put("ordf",     "\u00AA");
        htmlEnts.put("laquo",    "\u00AB");
        htmlEnts.put("not",      "\u00AC");
        htmlEnts.put("shy",      "\u00AD");
        htmlEnts.put("reg",      "\u00AE");
        htmlEnts.put("macr",     "\u00AF");
        htmlEnts.put("deg",      "\u00B0");
        htmlEnts.put("plusmn",   "\u00B1");
        htmlEnts.put("sup2",     "\u00B2");
        htmlEnts.put("sup3",     "\u00B3");
        htmlEnts.put("acute",    "\u00B4");
        htmlEnts.put("micro",    "\u00B5");
        htmlEnts.put("para",     "\u00B6");
        htmlEnts.put("middot",   "\u00B7");
        htmlEnts.put("cedil",    "\u00B8");
        htmlEnts.put("sup1",     "\u00B9");
        htmlEnts.put("ordm",     "\u00BA");
        htmlEnts.put("raquo",    "\u00BB");
        htmlEnts.put("frac14",   "\u00BC");
        htmlEnts.put("frac12",   "\u00BD");
        htmlEnts.put("frac34",   "\u00BE");
        htmlEnts.put("iquest",   "\u00BF");
        htmlEnts.put("Agrave",   "\u00C0");
        htmlEnts.put("Aacute",   "\u00C1");
        htmlEnts.put("Acirc",    "\u00C2");
        htmlEnts.put("Atilde",   "\u00C3");
        htmlEnts.put("Auml",     "\u00C4");
        htmlEnts.put("Aring",    "\u00C5");
        htmlEnts.put("AElig",    "\u00C6");
        htmlEnts.put("Ccedil",   "\u00C7");
        htmlEnts.put("Egrave",   "\u00C8");
        htmlEnts.put("Eacute",   "\u00C9");
        htmlEnts.put("Ecirc",    "\u00CA");
        htmlEnts.put("Euml",     "\u00CB");
        htmlEnts.put("Igrave",   "\u00CC");
        htmlEnts.put("Iacute",   "\u00CD");
        htmlEnts.put("Icirc",    "\u00CE");
        htmlEnts.put("Iuml",     "\u00CF");
        htmlEnts.put("ETH",      "\u00D0");
        htmlEnts.put("Ntilde",   "\u00D1");
        htmlEnts.put("Ograve",   "\u00D2");
        htmlEnts.put("Oacute",   "\u00D3");
        htmlEnts.put("Ocirc",    "\u00D4");
        htmlEnts.put("Otilde",   "\u00D5");
        htmlEnts.put("Ouml",     "\u00D6");
        htmlEnts.put("times",    "\u00D7");
        htmlEnts.put("Oslash",   "\u00D8");
        htmlEnts.put("Ugrave",   "\u00D9");
        htmlEnts.put("Uacute",   "\u00DA");
        htmlEnts.put("Ucirc",    "\u00DB");
        htmlEnts.put("Uuml",     "\u00DC");
        htmlEnts.put("Yacute",   "\u00DD");
        htmlEnts.put("THORN",    "\u00DE");
        htmlEnts.put("szlig",    "\u00DF");
        htmlEnts.put("agrave",   "\u00E0");
        htmlEnts.put("aacute",   "\u00E1");
        htmlEnts.put("acirc",    "\u00E2");
        htmlEnts.put("atilde",   "\u00E3");
        htmlEnts.put("auml",     "\u00E4");
        htmlEnts.put("aring",    "\u00E5");
        htmlEnts.put("aelig",    "\u00E6");
        htmlEnts.put("ccedil",   "\u00E7");
        htmlEnts.put("egrave",   "\u00E8");
        htmlEnts.put("eacute",   "\u00E9");
        htmlEnts.put("ecirc",    "\u00EA");
        htmlEnts.put("euml",     "\u00EB");
        htmlEnts.put("igrave",   "\u00EC");
        htmlEnts.put("iacute",   "\u00ED");
        htmlEnts.put("icirc",    "\u00EE");
        htmlEnts.put("iuml",     "\u00EF");
        htmlEnts.put("eth",      "\u00F0");
        htmlEnts.put("ntilde",   "\u00F1");
        htmlEnts.put("ograve",   "\u00F2");
        htmlEnts.put("oacute",   "\u00F3");
        htmlEnts.put("ocirc",    "\u00F4");
        htmlEnts.put("otilde",   "\u00F5");
        htmlEnts.put("ouml",     "\u00F6");
        htmlEnts.put("divide",   "\u00F7");
        htmlEnts.put("oslash",   "\u00F8");
        htmlEnts.put("ugrave",   "\u00F9");
        htmlEnts.put("uacute",   "\u00FA");
        htmlEnts.put("ucirc",    "\u00FB");
        htmlEnts.put("uuml",     "\u00FC");
        htmlEnts.put("yacute",   "\u00FD");
        htmlEnts.put("thorn",    "\u00FE");
        htmlEnts.put("yuml",     "\u00FF");

        // Symbols, mathematical symbols, and Greek letters
        htmlEnts.put("fnof",     "\u0192");

        htmlEnts.put("Alpha",    "\u0391");
        htmlEnts.put("Beta",     "\u0392");
        htmlEnts.put("Gamma",    "\u0393");
        htmlEnts.put("Delta",    "\u0394");
        htmlEnts.put("Epsilon",  "\u0395");
        htmlEnts.put("Zeta",     "\u0396");
        htmlEnts.put("Eta",      "\u0397");
        htmlEnts.put("Theta",    "\u0398");
        htmlEnts.put("Iota",     "\u0399");
        htmlEnts.put("Kappa",    "\u039A");
        htmlEnts.put("Lambda",   "\u039B");
        htmlEnts.put("Mu",       "\u039C");
        htmlEnts.put("Nu",       "\u039D");
        htmlEnts.put("Xi",       "\u039E");
        htmlEnts.put("Omicron",  "\u039F");
        htmlEnts.put("Pi",       "\u03A0");
        htmlEnts.put("Rho",      "\u03A1");
        htmlEnts.put("Sigma",    "\u03A3");
        htmlEnts.put("Tau",      "\u03A4");
        htmlEnts.put("Upsilon",  "\u03A5");
        htmlEnts.put("Phi",      "\u03A6");
        htmlEnts.put("Chi",      "\u03A7");
        htmlEnts.put("Psi",      "\u03A8");
        htmlEnts.put("Omega",    "\u03A9");

        htmlEnts.put("alpha",    "\u03B1");
        htmlEnts.put("beta",     "\u03B2");
        htmlEnts.put("gamma",    "\u03B3");
        htmlEnts.put("delta",    "\u03B4");
        htmlEnts.put("epsilon",  "\u03B5");
        htmlEnts.put("zeta",     "\u03B6");
        htmlEnts.put("eta",      "\u03B7");
        htmlEnts.put("theta",    "\u03B8");
        htmlEnts.put("iota",     "\u03B9");
        htmlEnts.put("kappa",    "\u03BA");
        htmlEnts.put("lambda",   "\u03BB");
        htmlEnts.put("mu",       "\u03BC");
        htmlEnts.put("nu",       "\u03BD");
        htmlEnts.put("xi",       "\u03BE");
        htmlEnts.put("omicron",  "\u03BF");
        htmlEnts.put("pi",       "\u03C0");
        htmlEnts.put("rho",      "\u03C1");
        htmlEnts.put("sigmaf",   "\u03C2");
        htmlEnts.put("sigma",    "\u03C3");
        htmlEnts.put("tau",      "\u03C4");
        htmlEnts.put("upsilon",  "\u03C5");
        htmlEnts.put("phi",      "\u03C6");
        htmlEnts.put("chi",      "\u03C7");
        htmlEnts.put("psi",      "\u03C8");
        htmlEnts.put("omega",    "\u03C9");
        htmlEnts.put("thetasym", "\u03D1");
        htmlEnts.put("upsih",    "\u03D2");
        htmlEnts.put("piv",      "\u03D6");

        // -- General Punctuation
        htmlEnts.put("bull",     "\u2022");
        htmlEnts.put("hellip",   "\u2026");
        htmlEnts.put("prime",    "\u2032");
        htmlEnts.put("Prime",    "\u2033");
        htmlEnts.put("oline",    "\u203E");
        htmlEnts.put("frasl",    "\u2044");

        htmlEnts.put("weierp",   "\u2118");
        htmlEnts.put("image",    "\u2111");
        htmlEnts.put("real",     "\u211C");
        htmlEnts.put("trade",    "\u2122");
        htmlEnts.put("alefsym",  "\u2135");

        htmlEnts.put("larr",     "\u2190");
        htmlEnts.put("uarr",     "\u2191");
        htmlEnts.put("rarr",     "\u2192");
        htmlEnts.put("darr",     "\u2193");
        htmlEnts.put("harr",     "\u2194");
        htmlEnts.put("crarr",    "\u21B5");
        htmlEnts.put("lArr",     "\u21D0");
        htmlEnts.put("uArr",     "\u21D1");
        htmlEnts.put("rArr",     "\u21D2");
        htmlEnts.put("dArr",     "\u21D3");
        htmlEnts.put("hArr",     "\u21D4");

        // -- Mathematical Operators
        htmlEnts.put("forall",   "\u2200");
        htmlEnts.put("part",     "\u2202");
        htmlEnts.put("exist",    "\u2203");
        htmlEnts.put("empty",    "\u2205");
        htmlEnts.put("nabla",    "\u2207");
        htmlEnts.put("isin",     "\u2208");
        htmlEnts.put("notin",    "\u2209");
        htmlEnts.put("ni",       "\u220B");
        htmlEnts.put("prod",     "\u220F");
        htmlEnts.put("sum",      "\u2211");
        htmlEnts.put("minus",    "\u2212");
        htmlEnts.put("lowast",   "\u2217");
        htmlEnts.put("radic",    "\u221A");
        htmlEnts.put("prop",     "\u221D");
        htmlEnts.put("infin",    "\u221E");
        htmlEnts.put("ang",      "\u2220");
        htmlEnts.put("and",      "\u2227");
        htmlEnts.put("or",       "\u2228");
        htmlEnts.put("cap",      "\u2229");
        htmlEnts.put("cup",      "\u222A");
        htmlEnts.put("int",      "\u222B");
        htmlEnts.put("there4",   "\u2234");
        htmlEnts.put("sim",      "\u223C");
        htmlEnts.put("cong",     "\u2245");
        htmlEnts.put("asymp",    "\u2248");
        htmlEnts.put("ne",       "\u2260");
        htmlEnts.put("equiv",    "\u2261");
        htmlEnts.put("le",       "\u2264");
        htmlEnts.put("ge",       "\u2265");
        htmlEnts.put("sub",      "\u2282");
        htmlEnts.put("sup",      "\u2283"); 
        htmlEnts.put("nsub",     "\u2284");
        htmlEnts.put("sube",     "\u2286");
        htmlEnts.put("supe",     "\u2287");
        htmlEnts.put("oplus",    "\u2295");
        htmlEnts.put("otimes",   "\u2297");
        htmlEnts.put("perp",     "\u22A5");
        htmlEnts.put("sdot",     "\u00B7");

        // -- Miscellaneous Technical
        htmlEnts.put("lceil",    "\u2308");
        htmlEnts.put("rceil",    "\u2309");
        htmlEnts.put("lfloor",   "\u230A");
        htmlEnts.put("rfloor",   "\u230B");
        htmlEnts.put("lang",     "\u2329");
        htmlEnts.put("rang",     "\u232A");

        // -- Geometric Shapes
        htmlEnts.put("loz",      "\u25CA");

        // -- Miscellaneous Symbols
        htmlEnts.put("spades",   "\u2660");
        htmlEnts.put("clubs",    "\u2663");
        htmlEnts.put("hearts",   "\u2665");
        htmlEnts.put("diams",    "\u2666");

        
        // Markup-significant and internationalization characters 
        // (e.g., for bidirectional text).
        
        htmlEnts.put("quot",     "\"");
        htmlEnts.put("amp",      "\u0026");
        htmlEnts.put("lt",       "\u003C");
        htmlEnts.put("gt",       "\u003E");

        htmlEnts.put("OElig",    "\u0152");
        htmlEnts.put("oelig",    "\u0153");
        htmlEnts.put("Scaron",   "\u0160");
        htmlEnts.put("scaron",   "\u0161");
        htmlEnts.put("Yuml",     "\u0178");

        // -- Spacing Modifier Letters
        htmlEnts.put("circ",     "\u02C6");
        htmlEnts.put("tilde",    "\u02DC");

        // -- General Punctuation
        htmlEnts.put("ensp",     "\u2002");
        htmlEnts.put("emsp",     "\u2003");
        htmlEnts.put("thinsp",   "\u2009");
        htmlEnts.put("zwnj",     "\u200C");
        htmlEnts.put("zwj",      "\u200D");
        htmlEnts.put("lrm",      "\u200E");
        htmlEnts.put("rlm",      "\u200F");
        htmlEnts.put("ndash",    "\u2013");
        htmlEnts.put("mdash",    "\u2014");
        htmlEnts.put("lsquo",    "\u2018");
        htmlEnts.put("rsquo",    "\u2019");
        htmlEnts.put("sbquo",    "\u201A");
        htmlEnts.put("ldquo",    "\u201C");
        htmlEnts.put("rdquo",    "\u201D");
        htmlEnts.put("bdquo",    "\u201E");
        htmlEnts.put("dagger",   "\u2020");
        htmlEnts.put("Dagger",   "\u2021");
        htmlEnts.put("permil",   "\u2030");
        htmlEnts.put("lsaquo",   "\u2039");
        htmlEnts.put("rsaquo",   "\u203A");
        htmlEnts.put("euro",     "\u20AC");

    }

    /**
     * Takes an string and unescape all HTML entities found in it. You can 
     * look at this function as a HTML to UTF-8 converter. Converts both 
     * numerical entites (decimal and hexadecimal) and namned entities.
     *
     * @param   the string to unescape
     * @return  the unescaped string
     */
    public static String unescapeHtml(String source) {
        String pattern = "(?i)(&([A-Za-z]+|\\#[0-9]+|\\#[xX][A-Fa-f0-9]+);)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            // Numerical entity
            if (m.group(2).startsWith("#")) {
                // TODO: What ranges are ok?
                char c = '\0';
                // Hexadecimal entity
                if(m.group(2).charAt(1) == 'x' || m.group(2).charAt(1) == 'X') {
                    c = (char) Integer.parseInt(m.group(2).substring(2), 16);
                // Decimal entity
                } else {
                    c = (char) Integer.parseInt(m.group(2).substring(1));
                }
                m.appendReplacement(sb, Character.toString(c));
            //String entity
            } else {
                String value = htmlEnts.get(m.group(2));
                if (value == null) {
                    // TODO: throw new java.io.IOException("Could not decode entity &" + m.group(2) +";");
                    System.out.println("Could not decode entity &" + m.group(2) +";");
                } else {
                    m.appendReplacement(sb, value);
                }
            }

        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     *
     * @param source
     * @return
     */
    public static String unescapeXml(String source) {
        // TODO: simplify and reuse the html unescapeHtml
        throw new java.lang.UnsupportedOperationException("Not implemented");
    }

    /**
     *
     * @param source
     * @return
     */
    public static String escapeHtml(String source) {
        throw new java.lang.UnsupportedOperationException("Not implemented");
    }

    /**
     *
     * @param source
     * @return
     */
    public static String escapeXml(String source) {
        throw new java.lang.UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Strip HTML/XML tags
     *
     * This function strips all tags found. The data inside the tags are left
     * intact. This is function can be used to convert HTML pages to pure text.
     * <p>
     * Note that you still need to unescape HTML entities and perhaps normlize
     * the whitespaces of you want to do this.
     *
     * @param source
     * @return
     */
    public static String stripHtml(String source){
        return source.replaceAll("\\<.*?>", "");
    }
    
    /**
     * Normilize whitespaces
     *
     * This function normalize whitespaces. It removes all groups of 
     * whitespaces (spaces, tabs, and line breaks) and replaces them with
     * just a single space.
     *
     * @param   source  The string to normalize
     * @return          The normalized  
     */
    public static String normalizeSpaces(String source) {
        return source.replaceAll("\\s+", " ");
    }

    /**
     * Apply unescapeHtml, stripHtml, and normalizeSpaces on a given String,
     * and return the result.
     * @param data the String to apply the functions on
     * @return the result after all functions has been applied
     * @see EscapeUtils.unescapeHtml
     * @see EscapeUtils.stripHtml
     * @see EscapeUtils.normalizeSpaces
     */
    public static String unescpaeAndStripHtml(String data) {
      data = EscapeUtils.unescapeHtml(data);
      data = EscapeUtils.stripHtml(data);
      data = EscapeUtils.normalizeSpaces(data);
      return data;
  }
}

