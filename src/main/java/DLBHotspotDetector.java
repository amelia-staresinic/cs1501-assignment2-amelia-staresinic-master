import java.util.*;

public class DLBHotspotDetector implements HotspotDetector {

    // ----------------------------
    // Interface methods (TODO)
    // ----------------------------
    Node root = new Node();

    @Override
    public void addLeakedPassword(String leakedPassword, int minN, int maxN) {
        if (leakedPassword == null)
            throw new IllegalArgumentException("null leakedPassword");
        if (minN < 1 || maxN < minN)
            throw new IllegalArgumentException("invalid n-range");
        

        Set<String> seen = new HashSet<>();

        for (int n = minN; n <= maxN; n++){
            for (int i = 0; i < leakedPassword.length(); i++){
                String s = leakedPassword.substring(i,i+n);
                Node node = this.root;
                for (char c : s.toCharArray()){
                    //insert chars as siblings or new child
                    Node subChar = new Node(c);
                    if(node.isTerminal){
                        node.child = subChar;
                    }
                    else if(node.child.ch != c){
                        //another for loop to check all siblings of child/check for repeated letter?
                        //or recursive helper method
                        if(node.child.sibling == null){
                            node.child.sibling = subChar;
                        }
                    }
                    node = subChar;
                    //mark as a leaf
                    node.isTerminal = true;  
                }
                //set up hotspot stats
                boolean isStart = false;
                boolean isEnd = false;
                if (i == 0){
                    isStart = true;
                    //increment begin count
                }
                else if(0 < i && i+n < leakedPassword.length()-1){
                    //increment mid count
                }
                else{
                    isEnd = true;
                    //increment end count
                }
                //check if seen
                if(!seen.contains(s)){
                    seen.add(s);
                    //increment docFreq -- add as parameter to Node?

                }
                //increment freq
                Hotspot hs = new Hotspot(s, minN, maxN, i, i, n, isStart, i, isEnd);
            }
        }
        // TODO:
        // 1) Optionally clear a per-password "seen" set to compute docFreq on first
        // occurrence only.
        // Set<String> seen = new HashSet<>();
        //
        // 2) For each n in [minN..maxN], slide a window over leakedPassword and extract
        // substrings.
        // For each substring s = leakedPassword[i..i+n):
        // - Insert s into the DLB (create nodes as needed).
        // - Mark terminal node, increment freq.
        // - If password not in 'seen', increment docFreq and add to 'seen'.
        // - Update begin/middle/end counts based on position i and (i+n == len).

    }

    @Override
    public Set<Hotspot> hotspotsIn(String candidatePassword) {
        if (candidatePassword == null)
            throw new IllegalArgumentException("null candidatePassword");

        // TODO:
        // - For i = 0..candidate.length()-1:
        // Start at the root of the DLB.
        // Walk character by character (candidate.charAt(j)) until no matching child
        // exists.
        // Every time you reach a terminal node, that substring is a hotspot → aggregate
        // it.
        return new LinkedHashSet<>();
    }

    private class Node{
        char ch;
        Node child, sibling;
        boolean isTerminal;
        Hotspot hotspot;

        public Node(){
            this.child = null;
            this.sibling = null;
            this.isTerminal = true;
        }
        public Node(char ch){
            this.ch = ch;
            this.child = null;
            this.sibling = null;
            this.isTerminal = false;
            this.hotspot = null;
        }


    }
}
