package com.github.jadamon42.family.service.parser;

import com.github.jadamon42.family.model.GenealogicalLink;
import com.github.jadamon42.family.repository.CustomCypherQueryExecutor;

public class GenealogicalLinkParserFactory {
    public static GenealogicalLinkParser getParser(GenealogicalLink link, CustomCypherQueryExecutor executor) {
        if (link.getPersonFromMarriedIn() && link.getPersonToMarriedIn()) {
            return new BothMarriedInLinkParser(executor);
        } else if (link.getPersonFromMarriedIn()) {
            return new PersonFromMarriedInLinkParser(executor);
        } else if (link.getPersonToMarriedIn()) {
            return new PersonToMarriedInLinkParser(executor);
        }
        return new NoneMarriedInLinkParser(executor);
    }
}
