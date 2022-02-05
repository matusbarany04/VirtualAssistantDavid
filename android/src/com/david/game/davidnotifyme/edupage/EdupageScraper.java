package com.david.game.davidnotifyme.edupage;

import android.icu.text.LocaleDisplayNames;
import android.util.Log;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.david.game.debug.Debugger;

import java.io.IOException;
import java.net.MalformedURLException;

public class EdupageScraper {

    public EdupageScraper() {
        Log.d("hey", " ");
    }

    public void init() {

    }

    public void scrape(String classname) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String TAG = "EdupageScraper";
                Log.d(TAG, "starting to scrape");
                try (final WebClient webClient = new WebClient(BrowserVersion.EDGE)) {
                    webClient.getOptions().setThrowExceptionOnScriptError(false);
                    final HtmlPage page = webClient.getPage("https://spseke.edupage.org/timetable/");
//                        Debugger.assertEquals("HtmlUnit – Welcome to HtmlUnit", page.getTitleText());
                    final String pageAsXml = page.asXml();

                    Log.d(TAG, pageAsXml);
                    //final String pageAsText = page.asNormalizedText();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


}
