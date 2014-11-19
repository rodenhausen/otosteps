CREATE TABLE IF NOT EXISTS `oto_collection` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` varchar(100) NOT NULL,
  `secret` varchar(100) NOT NULL,
  `lastretrieved` TIMESTAMP NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `oto_term` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `bucket` bigint(20) unsigned NOT NULL,
  `term` varchar(100) NOT NULL,
  `original_term` varchar(100) NOT NULL,
  `useless` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `oto_term_comment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `term` bigint(20) unsigned NOT NULL,
  `user` varchar(100) NOT NULL,
  `comment` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `oto_bucket` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `collection` bigint(20) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(1000),
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `oto_label` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `collection` bigint(20) unsigned NOT NULL,
  `type` varchar(100),
  `name` varchar(100) NOT NULL,
  `description` varchar(1000),
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

CREATE TABLE IF NOT EXISTS `oto_labeling` (
  `term` bigint(20) unsigned NOT NULL,
  `label` bigint(20) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `termlabel` (`term`,`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `oto_synonym` (
  `mainterm` bigint(20) unsigned NOT NULL,
  `label` bigint(20) unsigned NOT NULL,
  `synonymterm` bigint(20) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `synonym` (`mainTerm`,`label`,`synonymTerm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Context stuff: Given with upload
--
CREATE TABLE IF NOT EXISTS `oto_context` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `collection` bigint(20) unsigned NOT NULL,
  `source` varchar(100) NOT NULL,
  `text` text NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0;


-- Not needed: Info can be extracted from tables above
--CREATE TABLE IF NOT EXISTS `location` (}


-- Will retrieved directly from ontology; maybe later as a cache
--CREATE TABLE IF NOT EXISTS `ontology` (
--  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
--  `source` varchar(100) NOT NULL,
--  `sentence` varchar(300) NOT NULL,
--  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
--  PRIMARY KEY (`id`),
--  UNIQUE KEY `id` (`id`)
--) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

