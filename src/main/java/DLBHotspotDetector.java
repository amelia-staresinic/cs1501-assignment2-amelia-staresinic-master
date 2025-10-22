import java.util.*;

public class DLBHotspotDetector implements HotspotDetector {

    //empty root node to start
    Node root = new Node();

    @Override
    public void addLeakedPassword(String leakedPassword, int minN, int maxN) {
        if (leakedPassword == null)
            throw new IllegalArgumentException("null leakedPassword");
        if (minN < 1 || maxN < minN)
            throw new IllegalArgumentException("invalid n-range");
        

        Set<String> seen = new HashSet<>();

        for (int n = minN; n <= maxN; n++){
            for (int i = 0; i+n <= leakedPassword.length(); i++){
                String s = leakedPassword.substring(i,i+n);
                Node node = this.root;
                for (char c : s.toCharArray()){
                    //insert chars as siblings or new child
                    if(node.child == null){
                        node.child = new Node(c);
                        node = node.child;
                    }
                    else{
                        //helper method to check siblings or add node as sibling
                        node = addNode(node.child, c);
                    }
                }
                //mark as a leaf 
                node.isTerminal = true;
            
                if (i == 0){
                    node.beginCount += 1;
                    //increment begin count
                }
                else if(0 < i && i+n < leakedPassword.length()){
                    //increment mid count
                    node.midCount += 1;
                }
                else{
                    node.endCount += 1;
                    //increment end count
                }
                //check if seen
                if(!seen.contains(s)){
                    seen.add(s);
                    //increment docFreq only if not in seen
                    node.docFreq += 1;
                }
                //increment freq always
                node.freq += 1;
            }
        }

    }

    private Node addNode(Node n, char c){
        Node curr = n;
        while(curr != null){
            //returns existing node if c already in sibling list
            if(curr.ch == c){
                return curr;
            }
            if(curr.sibling == null){
                break;
            }

            curr = curr.sibling; 
        }
        //creates new sibling if new char
        Node subChar = new Node(c);
        curr.sibling = subChar;
        return subChar;
    }

    @Override
    public Set<Hotspot> hotspotsIn(String candidatePassword) {
        Set<Hotspot> hotspotSet = new LinkedHashSet<>();
        if (candidatePassword == null)
            throw new IllegalArgumentException("null candidatePassword");

        for(int i = 0; i < candidatePassword.length(); i++){
            Node node = root.child;
            StringBuilder subStr = new StringBuilder();
            if(node != null){
                for(int j = i; j < candidatePassword.length(); j++){
                    char c = candidatePassword.charAt(j);

                    //helper method returns existing node or null
                    node = checkSiblings(node, c);
                    
                    //substring not a hotspot
                    if(node == null){
                        break;
                    }
                    subStr.append(c);

                    //add or replace hotspot if at end of hotspot
                    if(node.isTerminal){
                        //check position
                        boolean atBegin = false;
                        boolean atEnd = false;
                        int midCount = 0;
                        if(i==0){
                            atBegin = true;
                        }
                        if(i+subStr.length() == candidatePassword.length()){
                            atEnd = true;
                        }
                        else{
                            midCount++;
                        }
                        Hotspot hs = null;
                        //check for existing hotspot
                        for(Hotspot h : hotspotSet){
                            if(h.ngram.equals(subStr.toString())){
                                //set new hotspot to equal existing
                                hs = h;
                                break;
                            }
                        }
                        if(hs == null){//if new hotspot in password
                            hs = new Hotspot(subStr.toString(), node.freq, node.docFreq, node.beginCount, node.midCount, node.endCount, atBegin, midCount, atEnd);
                            hotspotSet.add(hs);
                        }
                        else{ //if already recorded update stats and replace the hotspot
                            atBegin = atBegin || hs.candidateAtBegin;
                            atEnd = atEnd || hs.candidateAtEnd;
                            midCount += hs.candidateMiddleCount;
                            hotspotSet.remove(hs);
                            hotspotSet.add(new Hotspot(subStr.toString(), node.freq, node.docFreq, node.beginCount, node.midCount, node.endCount, atBegin, midCount, atEnd));
                        }
                    }
                    node = node.child;
                }
            }
        }

        return hotspotSet;
    }

    private Node checkSiblings(Node node, char c){
        Node curr = node;
        while(curr != null){
            if(curr.ch == c){
                return curr;
            }
            else{
                curr = curr.sibling;
            }
        }
        //return null if c not found
        return null;
    }

    private class Node{
        char ch;
        Node child, sibling;
        boolean isTerminal;
        int beginCount;
        int midCount;
        int endCount;
        int docFreq;
        int freq;

        public Node(){
            this.child = null;
            this.sibling = null;
            this.isTerminal = false;
        }
        public Node(char ch){
            this.ch = ch;
            this.child = null;
            this.sibling = null;
            this.isTerminal = false;
        }


    }
}
