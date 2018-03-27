package com.linkbynet.ska.newsCollector

import scala.collection.JavaConversions._

import java.net.URL;
import java.io.InputStreamReader

// JSoup web scraping library
import org.jsoup.Jsoup

// ROME RSS handling library
import com.rometools.rome.feed.synd._
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader

class NewsSource(val title: String, val uri: String)
class NewsItem(val source: NewsSource, val title: String, val uri: String, contentSource: => String) {
	lazy val content = contentSource
}


object Main extends App {
	val input = new SyndFeedInput();
	val sources = Seq(
		"https://blogs.microsoft.com/feed/",
		"https://aws.amazon.com/new/feed/"
	)
	def getItems(feedUrl: String) = {
		val feed = input.build(new XmlReader(new URL(feedUrl)));
		val source = new NewsSource(feed.getTitle, feed.getUri)
		feed.getEntries.toList.par map { e => 
			lazy val content = { println(s"Fetching ${e.getLink}"); Jsoup.connect(e.getLink).get.html }
			new NewsItem(source, e.getTitle, e.getLink, content) 
		}
	}
	sources flatMap getItems filter { _.title.toLowerCase.matches(".*intell.*") } foreach { x => println(
s"""---	
${x.title}
${x.uri}
---
""")}
}
