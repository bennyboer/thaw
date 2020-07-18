package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.impl.Article;

import java.util.Set;

/**
 * Handler for article sources.
 */
public class ArticleHandler extends AbstractAPAHandler {

    @Override
    public Set<SourceType> supports() {
        return Set.of(SourceType.ARTICLE);
    }

    @Override
    public String buildReferenceListEntry(Source source) {
        Article article = (Article) source;

        String authorStr = authorListToString(article.getAuthors(), false);

        // Last name, Initials. (Year). Article title. Journal Name, Volume(issue), page range. https://doi.org/xxxx
        return String.format(
                "%s%s (%d). %s. *%s*, %d(%d), %s. %s",
                authorStr,
                authorStr.endsWith(".") ? "" : ".",
                article.getYear(),
                article.getTitle(),
                article.getJournalName(),
                article.getVolume(),
                article.getNumber(),
                article.getPages(),
                article.getDoi()
        );
    }

    @Override
    public String buildInTextCitation(Source source, String position) {
        Article article = (Article) source;

        String authorStr = authorListToString(article.getAuthors(), true);

        if (position != null) {
            return String.format("%s, %d, %s", authorStr, article.getYear(), position);
        } else {
            return String.format("%s, %d", authorStr, article.getYear());
        }
    }

}
