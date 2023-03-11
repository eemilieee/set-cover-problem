import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Task2 extends Task
{
    public int N,M,K;
    public int [][]sets;
    public int [][]variables;
    public String result;
    public int []foundSets;
    public int noFoundSets;
    public int noVariables;

    public Task2() {
        this.sets = new int[1000][1000];
        this.variables = new int[1000][1000];
        this.foundSets = new int[1000];
    }

    public void init() {
        int i,j;
        for(i = 1; i <= this.M; i++)
            for(j = 1; j <= this.K; j++)
                this.variables[i][j] = 0;
        this.noFoundSets = 0;
        this.noVariables = 0;
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();

        boolean ok = true;

        while(ok)
        {
            formulateOracleQuestion();
            askOracle();
            decipherOracleAnswer();
            if(this.result.compareTo("True") == 0)
                ok = false;
            else
            {
                init();
                this.K += 1;
            }

            if(this.K > this.M)
                ok = false;
        }

        this.writeAnswer();
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

    @Override
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
        this.K = 1;

        for(i = 1; i <= nr3; i++)
        {
            int card = Integer.parseInt(in.readLine());
            nrc = 0;
            position = 1;

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
                        nrc++;
                    }
                }

                this.sets[i][0] = nrc;
            }
        }
    }

    private boolean hasElement(int []line, int length, int element) {
        int i;

        for(i = 1; i <= length; i++)
            if(element == line[i])
                return true;

        return false;
    }

    private int combinations(int n) {
        return n*(n-1)/2;
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        int i, j, k, r, s, card, nrVar, nrC;

        for(i = 1; i <= this.M; i++)
            for(j = 1; j <= this.K; j++)
                this.variables[i][j] = (i-1) * this.K + j;

        File file = new File("sat.cnf");
        FileWriter out = new FileWriter(file);

        // calculul numarului de variabile si a celui de clauze

        nrVar = this.M * this.K;
        nrC = this.K + this.M * combinations(this.K) + this.N + this.K * combinations(this.M);

        out.write("p cnf " + nrVar + " " + nrC + "\n");

        // afisarea primui tip de clauze (existenta a K submultimi ce compun acoperirea)

        for(i = 1; i <= this.K; i++)
        {
            for(j = 1; j <= this.M; j++)
                out.write(this.variables[j][i] + " ");
            out.write(0 + "\n");
        }

        // afisarea celui de-al doilea tip de clauze (posibilitatea de alegere a unei submultimi o
        // singura data in cadrul acoperirii)

        for(i = 1; i <= this.M; i++)
        {
            for(r = 1; r <= this.K; r++)
                for(s = r+1; s <= this.K; s++)
                {
                    out.write("-" + this.variables[i][r] + " -" + this.variables[i][s] + " ");
                    out.write(0 + "\n");
                }
        }

        for(i = 1; i <= this.K; i++)
        {
            for(r = 1; r <= this.M; r++)
                for(s = r+1; s <= this.M; s++)
                {
                    out.write("-" + this.variables[r][i] + " -" + this.variables[s][i] + " ");
                    out.write(0 + "\n");
                }
        }

        // al treilea tip de clauze (fiecare element din multimea initiala se sa
        // regaseasca in cel putin o submultime)

        for(i = 1; i<= this.N; i++)
        {
            for(j = 1; j <= this.M; j++)
            {
                card = this.sets[j][0];

                if(hasElement(this.sets[j], card, i))
                {
                    for(k = 1; k <= this.K; k++)
                        out.write(this.variables[j][k] + " ");
                }
            }
            out.write(0 + "\n");
        }

        out.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {

        FileInputStream file = new FileInputStream("sat.sol");
        BufferedReader in = new BufferedReader(new InputStreamReader(file));

        this.result = in.readLine();

        if(this.result.compareTo("False") == 0)
            return;

        this.noVariables = Integer.parseInt(in.readLine());

        StringTokenizer nr = new StringTokenizer(in.readLine(), " \n");

        int i, variable, k, m;
        boolean ok;

        for(i = 1; i <= this.noVariables; i++)
        {
            variable = Integer.parseInt(nr.nextToken());
            if(variable > 0)
            {
                ok = false;
                k = m = 1;

                // identificarea submultimii ce apartine solutiei si a indicelui
                // acesteia in acoperire corespunzatoare variabilelor boolene ce
                // au valoarea "true"

                while(!ok && m <= this.M)
                {
                    if(variable == this.variables[m][k])
                        ok = true;
                    else
                    {
                        if(k < this.K)
                            k++;
                        else
                        {
                            k = 1;
                            m++;
                        }
                    }
                }

                if(ok)
                {
                    this.foundSets[k - 1] = m;
                    this.noFoundSets++;
                }
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {

        if(this.result.compareTo("False") == 0)
            return;

        System.out.println(this.noFoundSets);

        int i;
        for(i = 0; i < this.noFoundSets; i++)
            System.out.print(this.foundSets[i] + " ");
    }

    public static void main(String [] args) throws IOException, InterruptedException {
        Task2 task = new Task2();
        task.solve();
    }
}

