build: trial rise redemption

run_trial:
	java Task1

run_rise:
	java Task2

run_redemption:
	java Task3

trial: Task1.java
	javac $^

rise: Task2.java
	javac $^

redemption: Task3.java
	javac $^

clean:
	rm -f *.class

.PHONY: build clean
