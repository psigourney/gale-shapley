/*****************************
 * Social Computing - Fall 2018
 * University of Texas at Austin
 * Prof Garg
 * Homework 2 - Problem 4
 * Gale-Shapley Stable Marriage Problem
 * Patrick Sigourney
 * Robert Pate
 *****************************/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class SMP {

    static class Preference{
        private int id;
        private int prefValue;

        private Preference(int idParam, int prefParam){
            id = idParam;
            prefValue = prefParam;
        }
        @Override public String toString(){
            return "(id" + String.valueOf(id) + ", pref" + String.valueOf(prefValue) + ")";
        }
    }

    static class Node {
        private char group;  //'m' or 'w'
        private int id;
        private LinkedList<Preference> prefs;
        private int matchedToId = 0;             //NodeId of the node I have been matched with
        private Map<Integer, Boolean> proposedTo; //Has this node proposed to nodeId?

        private Node(char groupParam, int idParam, int n) {
            group = groupParam;
            id = idParam;
            prefs = new LinkedList<Preference>();
            proposedTo = new HashMap<Integer, Boolean>();
            for(int i = 1; i <= n; i++) {
                proposedTo.put(i, false);
            }
        }

        @Override
        public String toString() {
            return String.valueOf(group) + String.valueOf(id) + ": " + prefs;
        }

        private int findPreference(int suitorId){
            for(Preference p : this.prefs){
                if(p.id == suitorId) return p.prefValue;
            }
            System.out.println("ERROR: findPreference() is null");
            System.exit(2);

            return -1;
        }

        private int findBestMatch() {
            //Return the nodeID of the most preferential match which has not already been proposed to

            Preference highestPref = null;

            //Seed highestPref with any available match
            for (Preference p : prefs) {
                if (!this.proposedTo.get(p.id)) {
                    highestPref = p;
                    break;
                }
            }
            if (highestPref == null) {
                System.out.println("ERROR: findBestMatch() highestPref is null");
                System.exit(2);
            }

            //Find the highest available match
            for (Preference p : prefs) {
                if (!this.proposedTo.get(p.id)) {
                    if (p.prefValue <= highestPref.prefValue)
                        highestPref = p;
                }
            }
        return highestPref.id;
        }
    }

    private static Node findNodeById(LinkedList<Node> nodeList, int nodeId){
        for(Node n : nodeList){
            if(n.id == nodeId)
                return n;
        }
        System.out.println("ERROR: findNodeByID() returned null");
        System.exit(2);

        return null;
    }

    private static boolean isBetterMatch(Node matchedNode, int suiterId){
        //Is the suitor a better match than matchedNode's current match?
        int suitorRank = matchedNode.findPreference(suiterId);
        int currentRank = matchedNode.findPreference(matchedNode.matchedToId);
        return (suitorRank < currentRank);
    }

    private static Map <Character, LinkedList <Node>> loadInput(String inputFile){
        //Return object will be a Map with 2 keys: m and w. The values will be a linked list of the m or w nodes
        Map <Character, LinkedList <Node>> nodeList = new HashMap<Character, LinkedList<Node>>();
        nodeList.put('m', new LinkedList<Node>());
        nodeList.put('w', new LinkedList<Node>());

        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            int n = Integer.valueOf(br.readLine());
            for(int i = 1; i <= n; i++){        //First n rows are the men
                String line = br.readLine();
                String[] arrLine = (line.split("\\s+"));

                Node aNode = new Node('m', i, n);

                for(int j = 1; j <= n; j++){    //For each man, populate preferences
                    //Preference is (id, prefValue)
                    Preference aPref = new Preference(Integer.valueOf(arrLine[j-1]), j);
                    aNode.prefs.add(aPref);
                }
                nodeList.get('m').add(aNode);
            }

            for(int i = 1; i <= n; i++){        //Second n rows are the women
                String line = br.readLine();
                String[] arrLine = (line.split("\\s+"));

                Node aNode = new Node('w', i, n);

                for(int j = 1; j <= n; j++){    //For each woman, populate preferences
                    //Preference is (id, prefValue)
                    Preference aPref = new Preference(Integer.valueOf(arrLine[j-1]), j);
                    aNode.prefs.add(aPref);
                }
                nodeList.get('w').add(aNode);
            }
        }catch (Exception e) {System.out.println("loadInput() Exception!: " + e);}
        return nodeList;
    }

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("java SMP <inputfile.txt> <m|w>");
            return;
        }
        if(args[1].charAt(0) != 'm' && args[1].charAt(0) != 'w'){
            System.out.println("Second argument must be m or w");
            System.out.println("Second argument: " + args[1].charAt(0));
            return;
        }

        char suitorGroup = args[1].charAt(0);
        char suiteeGroup;
        if(suitorGroup == 'm') suiteeGroup = 'w';
        else suiteeGroup = 'm';

        Map <Character, LinkedList <Node>> AllNodes = loadInput(args[0]);

        //Add all the nodes of the specified gender-optimality
        Queue<Node> freeNodes = new LinkedList<Node>(AllNodes.get(suitorGroup));

        Map<Integer, Integer> matchings = new HashMap<Integer, Integer>();

        while(!freeNodes.isEmpty()){
            Node suitor = freeNodes.poll();
            int bestMatchId = suitor.findBestMatch();
            Node candidateNode = findNodeById(AllNodes.get(suiteeGroup), bestMatchId);
            suitor.proposedTo.put(candidateNode.id, true);

            //Candidate is not matched, match suiter to this candidate.
            if(candidateNode.matchedToId == 0){
                candidateNode.matchedToId = suitor.id;
                matchings.put(suitor.id, candidateNode.id);
            }
            //Candidate is already matched, check if this is an improved matching
            else{
                if(isBetterMatch(candidateNode, suitor.id)){
                    //The suitor is a better match than the existing match
                    Node oldSuitor = findNodeById(AllNodes.get(suitorGroup), candidateNode.matchedToId);
                    oldSuitor.matchedToId = 0;

                    suitor.matchedToId = candidateNode.id;
                    candidateNode.matchedToId = suitor.id;
                    matchings.remove(oldSuitor.id);
                    matchings.put(suitor.id, candidateNode.id);

                    freeNodes.add(oldSuitor);
                }
                else{
                    freeNodes.add(suitor);
                }
            }
        }

        System.out.println("Input file: " + args[0]);
        System.out.println("Optimal for: " + suitorGroup);

        System.out.println("Matchings: ");
        for(Integer a : matchings.keySet()){
            System.out.println("(" + String.valueOf(a) + "," + matchings.get(a) + ")");
        }
    }
}
