package com.mobileproto.hireddit.hireddit.reddit;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

/**
 * Created by david on 12/6/15.
 */
public class HighestUpvoteCommentAsync {

    private static final String DEBUG_TAG = "Upvote Debug";
    private static final String ERROR_TAG = "Upvote Error";

    private RedditSearcher.CommentCallback myCommentCallback;
    private List<String[]> allComments;
    private Integer[] scores;

    private class SingleCommentSearch extends AsyncTask<Void, Void, Integer> {
        String url;
        int index;

        public SingleCommentSearch(String url, int index) {
            this.url = url;
            this.index = index;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int score = 0;
            try {
                Log.d(DEBUG_TAG, url);
                Document doc = Jsoup.connect(url).get();
                Element commentArea = doc.select("div.content").first().select("div.commentarea").first();
                Element siteTable = commentArea.select("div.sitetable.nestedlisting").first();
                Element tagline = siteTable.select("p.tagline").first();
                Element scoreElement = tagline.select("span.score.unvoted").first();
                String text = scoreElement.ownText();
                score = Integer.parseInt(text.substring(0, text.indexOf(' ')));
                Log.d(DEBUG_TAG, "Upvotes found: " + Integer.toString(score));

            } catch (IOException e) {
                Log.e(ERROR_TAG, "A Jsoup IOException occurred.");
            } catch (NullPointerException e) {
                Log.e(ERROR_TAG, "Could not find upvote score");
            }
            return score;
        }

        @Override
        protected void onPostExecute(Integer result) {
            scoreCallback(index, result);
        }
    }

    public HighestUpvoteCommentAsync(RedditSearcher.CommentCallback cc, List<String[]> allComments) {
        myCommentCallback = cc;
        this.allComments = allComments;
        scores = new Integer[allComments.size()];
    }

    protected void searchComments() {
        for (int i = 0; i < allComments.size(); i++) {
            String[] comment = allComments.get(i);
            SingleCommentSearch single = new SingleCommentSearch(comment[1], i);
            single.execute();
        }
    }

    private int getHighestScoringIndex() {
        int returnIndex = -1;
        int commentScore = Integer.MIN_VALUE;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > commentScore) {
                commentScore = scores[i];
                returnIndex = i;
            }
        }

        Log.d(DEBUG_TAG, "All comment requests complete. Comment picked with score " + Integer.toString(commentScore) + " at index " + Integer.toString(returnIndex));

        return returnIndex;
    }

    private void scoreCallback(int index, int score) {
        scores[index] = score;
        boolean isLast = true;
        for (Integer i : scores) {
            if (i == null) {
                isLast = false;
                break;
            }
        }

        if (isLast) {
            int highestIndex = getHighestScoringIndex();
            myCommentCallback.commentCallback(allComments.get(highestIndex)[0],
                    allComments.get(highestIndex)[1]);
        }

    }

}
