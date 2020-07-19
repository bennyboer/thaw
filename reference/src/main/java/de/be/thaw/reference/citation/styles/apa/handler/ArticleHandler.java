package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.impl.Article;

import java.util.Optional;
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

        String authorStr = authorListToString(article.getAuthors(), false, false);

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
    public String getCitePrefix(Citation citation) {
        return authorListToString(((Article) citation.getSource()).getAuthors(), true, citation.isDirect());
    }

    @Override
    public Optional<String> getCitePosition(Citation citation) {
        return Optional.ofNullable(citation.getPosition());
    }

    @Override
    public Optional<Integer> getCiteYear(Citation citation) {
        return Optional.ofNullable(((Article) citation.getSource()).getYear());
    }

}
