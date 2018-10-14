/*****************************
 * Social Computing - Fall 2018
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


public class SMP {

    static class Preference{
        public int id;
        public int pref;

        public Preference(int idParam, int prefParam){
            id = idParam;
            pref = prefParam;
        }
        @Override public String toString(){
            return "(id" + String.valueOf(id) + ", pref" + String.valueOf(pref) + ")";
        }
    }

    static class Node{
        public char group;  //'m' or 'w'
        public int id;
        public LinkedList<Preference> prefs;

        public Node(char groupParam, int idParam){
            group = groupParam;
            id = idParam;
            prefs = new LinkedList<Preference>();
        }

        @Override public String toString(){
            return String.valueOf(group) + String.valueOf(id) + ": " + prefs;
        }
    }



    private static Map <Character, LinkedList <Node>> loadInput(String inputFile){
        //Return object will be a Map with 2 keys: M and F. The values will be a linked list of the M or F nodes
        Map <Character, LinkedList <Node>> nodeList = new HashMap<Character, LinkedList<Node>>();
        nodeList.put('m', new LinkedList<Node>());
        nodeList.put('w', new LinkedList<Node>());

        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            int n = Integer.valueOf(br.readLine());
            for(int i = 1; i <= n; i++){        //First n rows are the men
                String line = br.readLine();
                String[] arrLine = (line.split("\\s+"));

                Node aNode = new Node('m', i);

                for(int j = 1; j <= n; j++){    //For each man, populate preferences
                    //Preference is (id, prefValue)
                    Preference aPref = new Preference(Integer.valueOf(arrLine[j-1]), Integer.valueOf(j));
                    aNode.prefs.add(aPref);
                }
                nodeList.get('m').add(aNode);
            }

            for(int i = 1; i <= n; i++){        //Second n rows are the women
                String line = br.readLine();
                String[] arrLine = (line.split("\\s+"));

                Node aNode = new Node('w', i);

                for(int j = 1; j <= n; j++){    //For each woman, populate preferences
                    //Preference is (id, prefValue)
                    Preference aPref = new Preference(Integer.valueOf(arrLine[j-1]), Integer.valueOf(j));
                    aNode.prefs.add(aPref);
                }
                nodeList.get('w').add(aNode);
            }
        }catch (Exception e) {System.out.println("LoadInput() Exception!: " + e);}
        return nodeList;
    }

    public static void main(String[] args) {
        if(args.length != 2){
            System.out.println("java SMP <inputfile.txt> <m|w>");
            return;
        }
        char optimal = args[1].charAt(0);
        Map <Character, LinkedList <Node>> AllNodes = loadInput(args[0]);

        System.out.println("Optimal: " + optimal);
        System.out.println("Men: " + AllNodes.get('m'));
        System.out.println("Women: " + AllNodes.get('w'));

    }
}
