import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Task3
{
    public int N,M;
    public int [][]sets;
    public String result;
    public int []foundSets;
    public int noFoundSets;
    public int[] containedElements;
    public int[] rarestElements;

    public Task3() {
        this.sets = new int[1000][1000];
        this.foundSets = new int[1000];
        this.containedElements = new int[1000];
        this.rarestElements = new int[1000];
        this.result = "False";
    }

    private int findElement(Set<String> set, String name) {
        int i = -1, nr = 0;

        for(String s : set)
        {
            if(s.compareTo(name) != 0)
                nr++;
            else
                i = nr;
        }

        return i;
    }

    private boolean hasElement(int []set, int el)
    {
        int i;
        for(i = 2; i <= set[0]+1; i++)
            if(set[i] == el)
                return true;
        return false;
    }

    private boolean hasUsedAllSets()
    {
        int i;
        for(i = 1; i <= this.M; i++)
            if(this.sets[i][1] == 0)
                return false;
        return true;
    }

    private boolean hasReconstructedSet()
    {
        int i;
        for(i = 1; i <= this.N; i++)
            if(this.containedElements[i] == 0)
                return false;
        return true;
    }

    private int chooseSet()
    {
        int i, max = 0, poz = 0, el;

        el = findRarestElement();
        for(i = 1; i <= this.M; i++)
            if(this.sets[i][1] == 0 && max < this.sets[i][0] && hasElement(this.sets[i], el))
            {
                max = this.sets[i][0];
                poz = i;
            }

        if(max != 0)
        {
            this.sets[poz][1] = 1;
            return poz;
        }

        return -1;
    }

    private int findRarestElement()
    {
        int i, poz = 1, min, el;
        while(this.rarestElements[poz] == 0)
            poz++;

        if(poz > this.N)
            return -1;

        min = this.rarestElements[poz];
        el = poz;

        for(i = poz + 1; i <= this.N; i++)
            if(min > this.rarestElements[i] && this.rarestElements[i] != 0)
            {
                min = this.rarestElements[i];
                el = i;
            }

        return el;
    }

    private void pickElements(int poz)
    {
        int i;
        for(i = 2; i <= this.sets[poz][0] + 1; i++)
        {
            this.containedElements[this.sets[poz][i]] += 1;
            this.rarestElements[this.sets[poz][i]] = 0;
        }

        this.foundSets[this.noFoundSets++] = poz;
    }

    public void solve()
    {
        int maxCardSet;

        while(!hasReconstructedSet() && !hasUsedAllSets())
        {
            maxCardSet = chooseSet();
            pickElements(maxCardSet);
        }

        if(hasReconstructedSet())
            this.result = "True";
    }

    public void readProblemData() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer nr = new StringTokenizer(in.readLine(), " \n");

        int i, j, nr1, nr2, nr3, nrc, position, element;
        nr1 = Integer.parseInt(nr.nextToken());
        nr2 = Integer.parseInt(nr.nextToken());
        nr3 = Integer.parseInt(nr.nextToken());

        Set<String> possessedCards = new LinkedHashSet<String>(nr1);
        Set<String> setOfCards = new LinkedHashSet<String>(nr2);

        for(i = 1; i <= nr1; i++)
            possessedCards.add(in.readLine());

        for(i = 1; i <= nr2; i++)
            setOfCards.add(in.readLine());

        // determinarea multimii initiale (fara dubluri)

        setOfCards.removeAll(possessedCards);

        this.N = setOfCards.size();
        this.M = nr3;

        for(i = 1; i <= nr3; i++)
        {
            int card = Integer.parseInt(in.readLine());
            nrc = 0;
            position = 2;

            // se extrag multimile existente: cardinalul si elementele ce le compun

            for(j = 1; j <= card; j++)
            {
                String c = in.readLine();
                if(setOfCards.contains(c))
                {
                    element = findElement(setOfCards, c);
                    if(element >= 0)
                    {
                        this.sets[i][position++] = element+1;
                        this.rarestElements[element+1]++;
                        nrc++;
                    }
                }

                this.sets[i][0] = nrc;
            }
        }
    }

    public void writeAnswer() throws IOException {

        if(this.result.compareTo("False") == 0)
            return;

        System.out.println(this.noFoundSets);

        int i;
        for(i = 0; i < this.noFoundSets; i++)
            System.out.print(this.foundSets[i] + " ");
    }

    public static void main(String [] args) throws IOException, InterruptedException {
        Task3 task = new Task3();
        task.readProblemData();

        task.solve();
        task.writeAnswer();
    }
}
