import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMPRP {

    public SMPRP(int numberOfProposers, boolean manOptimal, String[] manPrefs, String[] womanPrefs) {
        this.manOptimal = manOptimal;
        men = new ArrayList<person>(numberOfProposers);
        women = new ArrayList<person>(numberOfProposers);
        for (int i = 0; i <manPrefs.length ; i++) {
            men.add(buildPerson(i, manPrefs[i]));
        }
        for (int i = 0; i <womanPrefs.length ; i++) {
            women.add(buildPerson(i, womanPrefs[i]));
        }

    }

    person buildPerson(int id, String prefsList) {
        person p = new person(id);
        Scanner scanner = new Scanner(prefsList);
        while (scanner.hasNextInt()) {
            p.prefs.add((scanner.nextInt() - 1)); // 0-index to match our lists, we'll add the 1 back on printing matches
        }
        p.remainingPrefs = new LinkedList<Integer>(p.prefs);
        return p;
    }

    public void PrintStableMarriage() {
        this.printStableMarriage();
    };

    boolean manOptimal;
    ArrayList<person> men;
    ArrayList<person> women;
    class person {
        int id;
        ArrayList<Integer> prefs;
        LinkedList<Integer> remainingPrefs;
        person partner;

        public person(int id) {
            this.id = id;
            prefs = new ArrayList<Integer>();
        }
    }

    private void printStableMarriage() {
        if (manOptimal) {
            runXOptimal(men, women);
            for (person m: men) {
                System.out.printf("(%d,%d)\n", m.id + 1, m.partner.id + 1); // +1 because output should be 1 indexed
            }
        } else {
            runXOptimal(women, men);
            for (person w: women) {
                System.out.printf("(%d,%d)\n", w.id + 1, w.partner.id + 1);
            }
        }
    }

    private void runXOptimal(ArrayList<person> proposers, ArrayList<person> proposees) {
        LinkedList<person> freeProposers = new LinkedList<person>(proposers);
        while (!freeProposers.isEmpty()) {
            person er = freeProposers.pop();
            int eeId = er.remainingPrefs.pop();
            person ee = proposees.get(eeId);
            if (ee.id != eeId) System.out.println("Warning, our IDs are mismatched!");
            if (ee.partner == null) {
                engage(er, ee);
            } else {
                int proposerRank = ee.prefs.indexOf(er.id); // lower is better
                int partnerRank = ee.prefs.indexOf(ee.partner.id);
                if (proposerRank < partnerRank) { // prefers proposer to partner
                    ee.partner.partner = null; // free old partner
                    freeProposers.push(ee.partner);
                    engage(er, ee);
                } else { // proposee rejects proposer
                    freeProposers.push(er);
                }
            }
        }
    }

    private void engage(person proposer, person proposee) {
        proposee.partner = proposer;
        proposer.partner = proposee;
    }


    public static void main(String[] args) throws IOException {
        String path = args[0];
        String proposerType = args[1];
        Pattern proposerPattern = Pattern.compile("[MmWw]");
        Matcher m = proposerPattern.matcher((proposerType));
        BufferedReader br = null;
        int numberOfProposers; // also number of accepters
        String[] manPrefs, womanPrefs;
        boolean manOptimal;

        if (!m.find()) {
            System.out.println("2nd argument must be m or w.");
            return;
        } else {
            manOptimal = proposerType.matches("[Mm]");
        }

        try {
            br = new BufferedReader(new FileReader(path));
            numberOfProposers = Integer.valueOf(br.readLine());
            manPrefs = new String[numberOfProposers];
            womanPrefs = new String[numberOfProposers];
            for (int i = 0; i < numberOfProposers; i++) {
                manPrefs[i] = br.readLine();
            }
            for (int i = 0; i < numberOfProposers; i++) {
               womanPrefs[i] = br.readLine();
            }
        }
        finally {
            if (br != null) br.close();
        }

        /* Debugging input output:
        System.out.println("Inputs:");
        System.out.println(args[1] + " Optimal");
        System.out.println("n: " + numberOfProposers);
        System.out.println("man prefs:");
        for (int i = 0; i < manPrefs.length ; i++) {
            System.out.println(manPrefs[i]);
        }
        System.out.println("woman prefs:");
        for (int i = 0; i < manPrefs.length ; i++) {
            System.out.println(womanPrefs[i]);
        }
        */


        new SMPRP(numberOfProposers, manOptimal, manPrefs, womanPrefs).printStableMarriage();

        /* Debugging both man and woman optimal:
        System.out.println(args[0].substring(2,7) + " m");
        new SMPRP(numberOfProposers, true, manPrefs, womanPrefs).printStableMarriage();
        System.out.println("\n" + args[0].substring(2,7) + " w");
        new SMPRP(numberOfProposers, false, manPrefs, womanPrefs).printStableMarriage();
        System.out.println("\n");
        */
    }
}
