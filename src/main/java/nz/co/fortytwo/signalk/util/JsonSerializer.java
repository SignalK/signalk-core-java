package nz.co.fortytwo.signalk.util;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.text.*;
import java.io.*;

import mjson.Json;

/**
 * Class to read/write objects as JSON.
 * Can be overridden to support custom serialization. For writing this should involve
 * overriding the {@link #getCustomWrite} method like so:
 * <pre class="code">
 * public Object getCustomWrite(Object o) {
 *     if (o instanceof Widget) {
 *         Map m = new HashMap();
 *         m.put("type", "widget");
 *         m.put("name", widget.getName());
 *         return m;
 *     } else {
 *         return super.getCustomWrite(o);
 *     }
 * }
 * </pre>
 * and for reading the {@link #getCustomRead} methods:
 * <pre class="code">
 * public Object getCustomRead(Map m) {
 *     if ("widget".equals(m.get("type"))) {
 *         return Widget.getByName((String)m.get("name"));
 *     } else {
 *         return super.getCustomRead();
 *     }
 * }
 * </pre>
 *
 * Take from qtunes project, which is GPL
 * TODO: change this class to apache compatible serializer
 */
public class JsonSerializer {

    private boolean laxkey, pretty;
    private DecimalFormat df = new DecimalFormat("#0.000000");

    /**
     * Set to true to not quote the keys in a map - this is technically invalid,
     * but accepted by many.
     */
    public void setLaxKeyQuoting(boolean lax) {
        this.laxkey = lax;
    }

    /**
     * Set to true to pretty-print the output by indenting and adding newlines
     */
    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }

    /**
     * Set the maximum number of decimal places for any double and integer
     */
    public void setNumDecimalPlaces(int numdp) {
        StringBuilder sb = new StringBuilder();
        sb.append("#0.");
        for (int i=0;i<numdp;i++) {
            sb.append("0");
        }
        df = new DecimalFormat(sb.toString());
    }

    /**

    /**
     * Serialize the object and return the serialized version as a String
     * @param object the Object to serialize
     */
    public String write(Object object) {
        try {
            StringBuilder sb = new StringBuilder();
            write(object, sb);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize the object and write the serialized version to the specified {@link Appendable}
     * @param o the Object to serialize
     * @param sb the Appendable to write to
     */
    public void write(Object o, Appendable sb) throws IOException {
        write(o, sb, false, false, pretty ? new StringBuilder("\n") : null);
    }

    /**
     * As for {@link #write(Object,Appendable) but compound objects are synchronized on before iterating
     * @param o the Object to serialize
     * @param sb the Appendable to write to
     */
    public void writeSynchronized(Object o, Appendable sb)  throws IOException {
        write(o, sb, false, true, pretty ? new StringBuilder("\n") : null);
    }

    private void write(Object o, Appendable sb, boolean iskey, boolean synchronize, StringBuilder prefix) throws IOException {
        if (o instanceof StringBuffer || o instanceof StringBuilder || o instanceof Character) {
            o = o.toString();
        }
        if (o == null) {
            sb.append("null");
        } else if (o instanceof JSONSerializable) {
            ((JSONSerializable)o).write(sb, synchronize, prefix);
        } else if (o instanceof String) {
            String string = (String)o;
            if (string.length() == 0) {
                sb.append("\"\"");
            } else {
                char c = 0;
                int len = string.length();
                Appendable origsb = sb;
                boolean escape = false;
                if (iskey && laxkey) {
                    sb = new StringBuilder();
                } else {
                    sb.append('"');
                }
                for (int i = 0; i < len; i++) {
                    char b = c;
                    c = string.charAt(i);
                    switch (c) {
                    case '\\':
                    case '"':
                        sb.append('\\');
                        sb.append(c);
                        escape = true;
                        break;
                    case '/':
                        if (b == '<') {
                            sb.append('\\');
                        }
                        sb.append(c);
                        escape = true;
                        break;
                    case '\b':
                        sb.append("\\b");
                        escape = true;
                        break;
                    case '\t':
                        sb.append("\\t");
                        escape = true;
                        break;
                    case '\n':
                        sb.append("\\n");
                        escape = true;
                        break;
                    case '\f':
                        sb.append("\\f");
                        escape = true;
                        break;
                    case '\r':
                        sb.append("\\r");
                        escape = true;
                        break;
                    default:
                        if (c < 0x20 || (c >= 0x80 && c < 0xA0) || c == 0x2028 || c == 0x2029) {
                            String t = Integer.toHexString(c);
                            sb.append("\\u");
                            switch(t.length()) {
                                case 1: sb.append('0');
                                case 2: sb.append('0');
                                case 3: sb.append('0');
                            }
                            sb.append(t);
                            escape = true;
                        } else {
                            sb.append(c);
                            if (!((c>='a' && c<='z') || (c>='A' && c<='Z') || (i>0 && ((c>='0' && c<='9') || c=='.' || c=='-')) || c=='_')) {
                                escape = true;
                            }
                        }
                    }
                }
                if (origsb != sb) {
                    if (escape) {
                        origsb.append('"');
                        origsb.append((CharSequence)sb);
                        origsb.append('"');
                    } else {
                        origsb.append((CharSequence)sb);
                    }
                    sb = origsb;
                } else {
                    sb.append('"');
                }
            }
        } else if (o instanceof Boolean) {
            sb.append(o.toString());
        } else if (o instanceof Number) {
            Number n = (Number)o;
            if (n instanceof Double && (((Double)n).isInfinite() || ((Double)n).isNaN())) {
                throw new IllegalArgumentException("Infinite or NaN");
            } else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new IllegalArgumentException("Infinite or NaN");
            } else if (n instanceof Float || n instanceof Double) {
                String s = df.format(n);
                int l = s.length() - 1;
                if (s.indexOf('.') > 0 && s.indexOf('e') < 0 && s.indexOf('E') < 0) {
                    while (s.charAt(l) == '0') {
                        l--;
                    }
                    if (s.charAt(l) == '.') {
                        l--;
                    }
                }
                sb.append(s, 0, l + 1);
            } else {
                sb.append(n.toString());
            }
        } else if (o.getClass().isArray()) {
            sb.append("[");
            if (prefix != null) {
                prefix.append("  ");
                sb.append(prefix);
            }
            if (synchronize) {
                synchronized(o) {
                    int len = Array.getLength(o);
                    for (int i=0;i<len;i++) {
                        if (i != 0) {
                            sb.append(",");
                            if (prefix != null) {
                                sb.append(prefix);
                            }
                        }
                        write(Array.get(o, i), sb, false, synchronize, prefix);
                    }
                }
            } else {
                int len = Array.getLength(o);
                for (int i=0;i<len;i++) {
                    if (i != 0) {
                        sb.append(",");
                        if (prefix != null) {
                            sb.append(prefix);
                        }
                    }
                    write(Array.get(o, i), sb, false, synchronize, prefix);
                }
            }
            if (prefix != null) {
                prefix.setLength(prefix.length() - 2);
                sb.append(prefix);
            }
            sb.append("]");
        } else if (o instanceof Map) {
            sb.append("{");
            if (prefix != null) {
                prefix.append("  ");
                sb.append(prefix);
            }
            if (synchronize) {
                synchronized(o) {
                    boolean first = true;
                    for (Iterator i = ((Map)o).entrySet().iterator();i.hasNext();) {
                        Map.Entry e = (Map.Entry)i.next();
                        if (e.getKey() instanceof String && e.getValue()!=null) {
                            if (first) {
                                first = false;
                            } else {
                                sb.append(",");
                                if (prefix != null) {
                                    sb.append(prefix);
                                }
                            }
                            write(e.getKey(), sb, true, synchronize, null);
                            sb.append(":");
                            write(e.getValue(), sb, false, synchronize, prefix);
                        }
                    }
                }
            } else {
                boolean first = true;
                for (Iterator i = ((Map)o).entrySet().iterator();i.hasNext();) {
                    Map.Entry e = (Map.Entry)i.next();
                    if (e.getKey() instanceof String && e.getValue()!=null) {
                        if (first) {
                            first = false;
                        } else {
                            sb.append(",");
                            if (prefix != null) {
                                sb.append(prefix);
                            }
                        }
                        write(e.getKey(), sb, true, synchronize, null);
                        sb.append(":");
                        write(e.getValue(), sb, false, synchronize, prefix);
                    }
                }
            }
            if (prefix != null) {
               prefix.setLength(prefix.length() - 2);
               sb.append(prefix);
            }
            sb.append("}");
        } else if (o instanceof Collection) {
            sb.append("[");
            if (prefix != null) {
                prefix.append("  ");
                sb.append(prefix);
            }
            Collection list = (Collection)o;
            boolean first = true;
            if (synchronize) {
                synchronized(list) {
                    for (Iterator i=list.iterator();i.hasNext();) {
                        if (!first) {
                            sb.append(",");
                            if (prefix != null) {
                                sb.append(prefix);
                            }
                        }
                        first = false;
                        write(i.next(), sb, false, synchronize, prefix);
                    }
                }
            } else {
                for (Iterator i=list.iterator();i.hasNext();) {
                    if (!first) {
                        sb.append(",");
                        if (prefix != null) {
                            sb.append(prefix);
                        }
                    }
                    first = false;
                    write(i.next(), sb, false, synchronize, prefix);
                }
            }
            if (prefix != null) {
                prefix.setLength(prefix.length() - 2);
                sb.append(prefix);
            }
            sb.append("]");
        } else {
            write(getCustomWrite(o), sb, false, synchronize, prefix);
        }
    }

    /**
     * Get an object that can be serialized to represent the specified Object.
     * This method is called when there is no default way to serialize
     * the parameter - by default it throws an Exception saying the object
     * cannot be serialized. It can be overridden by subclasses.
     * @param object an unserializable Object
     * @return an Object which can be used in it's place
     */
    public Object getCustomWrite(Object object) throws IllegalArgumentException {
        throw new IllegalArgumentException("Unable to serialize "+object);
    }


    /**
     * Parse a JSON serialized String and return the Object it represents
     */
    public NavigableMap<String, Object> read(String s) throws IOException {
        return read(Json.read(s));
    }

    /**
     * Convert a json object to a flattened map of key/value pairs
     * 
     * @param json
     * @return NavigableMap useable in the signalk model
     */
    public NavigableMap<String, Object> read(Json json) {
		//get keys and recurse
    	ConcurrentSkipListMap<String, Object> map = new ConcurrentSkipListMap<String, Object>();
    	if(json.has(JsonConstants.VESSELS)){
    		recurseJsonFull(json,map,"");
    	}
    	if(json.has(JsonConstants.UPDATES)){
    		parseJsonDelta(json,map,"");
    	}
		return map;
	}




	private void recurseJsonFull(Json json, ConcurrentSkipListMap<String, Object> map, String prefix) {
		for(Entry<String, Json> entry: json.asJsonMap().entrySet()){
			if(entry.getValue().isPrimitive()){
				map.put(prefix+entry.getKey(), entry.getValue().getValue());
			}else if(entry.getValue().isArray()){
				map.put(prefix+entry.getKey(), entry.getValue().toString());
			}else{
				recurseJsonFull(entry.getValue(), map, prefix+entry.getKey()+".");
			}
		}
		
	}
	
	private void parseJsonDelta(Json json, ConcurrentSkipListMap<String, Object> map, String prefix) {
		
		
	}




	/**
     * An object can implement this interface if it wants to control
     * how it's written to the JSON stream
     */
    public static interface JSONSerializable {
        public void write(Appendable a, boolean synchronize, StringBuilder eol) throws IOException;
    }

    /*
    public static void main(String[] args) throws Exception {
        JSONSerializer json = new JSONSerializer();
        Object o = json.read(args[0]);
        String s = json.write(o);
        System.out.println(args[0]);
        System.out.println(o);
        System.out.println(s);
    }
    */
}

