/**
 * Copyright (C) 2013 HalZhang
 */
package net.dbanotes.startupnews.ui.fragments;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.dbanotes.startupnews.R;
import net.dbanotes.startupnews.entity.Comment;
import net.dbanotes.startupnews.entity.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * StartupNews
 * <p>
 * 评论
 * </p>
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class CommentsListFragment extends AbsBaseListFragment {
    
    @SuppressWarnings("unused")
    private static final String LOG_TAG = CommentsListFragment.class.getSimpleName();
    
    private ArrayList<Comment> mComments = new ArrayList<Comment>(24);
    
    private CommentsAdapter mAdapter;
    
    private CommentsTask mTask;
    
    private String mMoreUrl;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new CommentsAdapter();
        mTask = new CommentsTask();
        mTask.execute(getString(R.string.host, "/newcomments"));
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    protected void onPullUpListViewRefresh(PullToRefreshListView refreshListView) {
        super.onPullUpListViewRefresh(refreshListView);
        if(mTask != null){
            mTask.cancel(true);
            mTask = null;
        }
        mTask = new CommentsTask();
        mTask.execute(getString(R.string.host,mMoreUrl));
    }
    
    private class CommentsTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element body = doc.body();
                Elements commentSpans = body.select("span.comment");
                Elements comHeadSpans = body.select("span.comhead");
                if(!commentSpans.isEmpty()){
                    Iterator<Element> spanCommentIt = commentSpans.iterator();
                    Iterator<Element> spanComHeadIt = comHeadSpans.iterator();
                    Comment comment = null;
                    User user = null;
                    while(spanComHeadIt.hasNext() && spanCommentIt.hasNext()){
                        String commentText = spanCommentIt.next().text();
                        Element span = spanComHeadIt.next();
                        Elements as = span.getElementsByTag("a");
                        user = new User();
                        user.setId(as.get(0).text());
                        String link = as.get(1).attr("href");
                        String parent = as.get(2).attr("href");
                        String discuss = as.get(3).attr("href");
                        comment = new Comment();
                        comment.setUser(user);
                        comment.setLink(link);
                        comment.setParent(parent);
                        comment.setDiscuss(discuss);
                        comment.setText(commentText);
                        mComments.add(comment);
                    }
                }
                mMoreUrl = body.select("a[href^=/x?fnid=]").get(1).attr("href");
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                mAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_LONG).show();
            }
            getPullToRefreshListView().onRefreshComplete();
            mTask = null;
            super.onPostExecute(result);
        }
        
        @Override
        protected void onCancelled() {
            getPullToRefreshListView().onRefreshComplete();
            mTask = null;
            super.onCancelled();
        }
        
    }
    
    private class CommentsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mComments.size();
        }

        @Override
        public Object getItem(int position) {
            return mComments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.comment_list_item, null);
                holder.mUserId = (TextView)convertView.findViewById(R.id.comment_item_user_id);
                holder.mCreated = (TextView)convertView.findViewById(R.id.comment_item_created);
                holder.mCommentText = (TextView)convertView.findViewById(R.id.comment_item_text);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            Comment comment = mComments.get(position);
            holder.mUserId.setText(comment.getUser().getId());
            holder.mCreated.setText(comment.getCreated());
            holder.mCommentText.setText(comment.getText());
            
            return convertView;
        }
        
        class ViewHolder{
            TextView mUserId;
            TextView mCreated;
            TextView mCommentText;
        }
        
    }

    @Override
    public int getContentViewId() {
        return R.layout.ptr_list_layout;
    }

}
