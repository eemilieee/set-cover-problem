import java.io.*;
import java.util.StringTokenizer;

public class Task1 extends Task
{
    public int N,M,K;
    public int [][]sets;
    public int [][]variables;
    public String result;
    public int []foundSets;
    public int noFoundSets;
    public int noVariables;

    public Task1() {
        this.sets = new int[1000][1000];
        this.variables = new int[1000][1000];
        this.foundSets = new int[1000];
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        this.readProblemData();
        this.formulateOracleQuestion();
        askOracle();
        this.decipherOracleAnswer();
        this.writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer nr = new StringTokenizer(in.readLine(), " \n");

        this.N = Integer.parseInt(nr.nextToken());
        this.M = Integer.parseInt(nr.nextToken());
        this.K = Integer.parseInt(nr.nextToken());

        int i, j, card;
        for(i = 1; i <= this.M; i++)
        {
            // se extrag multimile existente: cardinalul si elementele ce le compun

            nr = new StringTokenizer(in.readLine(), " \n");
            card = Integer.parseInt(nr.nextToken());

            this.sets[i][0] = card;

            for(j = 1; j <= card; j++)
                this.sets[i][j] = Integer.parseInt(nr.nextToken());
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

        // initializarea matricei de variabile boolene
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

        System.out.println(this.result);

        if(this.result.compareTo("False") == 0)
            return;

        System.out.println(this.noFoundSets);

        int i;
        for(i = 0; i < this.noFoundSets; i++)
            System.out.print(this.foundSets[i] + " ");
    }

    public static void main(String [] args) throws IOException, InterruptedException {
        Task1 task = new Task1();
        task.solve();
    }
}
