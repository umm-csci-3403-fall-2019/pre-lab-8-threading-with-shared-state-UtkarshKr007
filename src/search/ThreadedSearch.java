package search;

import java.util.List;

public class ThreadedSearch<T> implements Searcher<T>, Runnable {

    private int numThreads;
    private T target;
    private List<T> list;
    private int begin;
    private int end;
    private Answer answer;

    public ThreadedSearch(int numThreads) {
        this.numThreads = numThreads;
    }

    private ThreadedSearch(T target, List<T> list, int begin, int end, Answer answer) {
        this.target = target;
        this.list = list;
        this.begin = begin;
        this.end = end;
        this.answer = answer;
    }

    /**
     * Searches `list` in parallel using `numThreads` threads.
     * <p>
     * You can assume that the list size is divisible by `numThreads`
     */
    public boolean search(T target, List<T> list) throws InterruptedException {

        // Create a instance of answer that will be shared by the threads
        Answer sharedAnswer = new Answer();
        // Create a thread array of size numThreads
        Thread[] threads = new Thread[numThreads];
        // Splitting the list in equal sizes for each thread
        int split = list.size()/numThreads;

        // For each thread
        for(int i=0;i<numThreads;i++) {
                // We create an instance of this class, pass it the same target, list and the shared answer instance but
                // change the begin and end value to split the list into equal sizes
                ThreadedSearch<T> threadedSearch = new ThreadedSearch<>(target, list, i * split, (i + 1) * split, sharedAnswer);
                // Pass the class to a thread stored in the thread array
                threads[i] = new Thread(threadedSearch);
                // Start that thread
                threads[i].start();
        }

        for(int i=0;i<numThreads;i++){
            // join waits for each thread to finish
            threads[i].join();
        }

        // return the current value of answer
        return sharedAnswer.getAnswer();
    }

    public void run() {
        for(int index=begin;index<end;index++) {
            // If answer has been found by some thread we exit the run() method
            if(answer.getAnswer()){
                return;
            }
            // If the value at current index in list is equal to target, this thread sets answer to true
            if(list.get(index).equals(target)){
                answer.setAnswerTrue();
                return;
            }
        }
    }

    private class Answer {
        // By default, answer has not been found so it is initialized as false.
        private boolean answer = false;

        // This retrieves the current value of answer
        boolean getAnswer() {
            return answer;
        }

        // Since no one will actually call this method with any value other than `true`, I changed it's function
        // to just set answer to true.
        synchronized void setAnswerTrue() {
            answer = true;
        }
    }
}
