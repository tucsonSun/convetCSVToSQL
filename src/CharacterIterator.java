import java.util.Iterator;

class CharacterIterator implements Iterator<Character> {

    private final String str;
    private int pos = -1;
    private Character perviousValue;
    private int previousPos = -1;

    public CharacterIterator(String str) {
        this.str = str;
    }

    public boolean hasNext() {
        return (pos + 1) < str.length();
    }

    public boolean hasPervious() {
        return perviousValue != null;
    }
    
    public Character next() {
    	previousPos = pos;
    	perviousValue = (pos >=0 ) ? str.charAt(pos) : null;
    	pos = pos + 1;
        return str.charAt(pos);
    }
    
    public Character perviousValue() {
        return perviousValue;
    }
    
    public int previousPos() {
        return previousPos;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}