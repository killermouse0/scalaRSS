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

case class NewsSource(title: String, uri: String)
case class NewsItem(source: NewsSource, title: String, uri: String, content: String)

object Main extends App {
	val input = new SyndFeedInput();
	val sources = Seq(
		"https://blogs.microsoft.com/feed/",
		"https://aws.amazon.com/new/feed/"
	)
	def getItems(feedUrl: String) = {
		val feed = input.build(new XmlReader(new URL(feedUrl)));
		val source = NewsSource(feed.getTitle, feed.getUri)
		feed.getEntries.toList.par map { e => 
			val content = Jsoup.connect(e.getLink).get.html
			NewsItem(source, e.getTitle, e.getLink, content) 
		}
	}
	sources flatMap getItems foreach { x => println(
s"""---	
${x.title}
${x.uri}
---
""")}
}
