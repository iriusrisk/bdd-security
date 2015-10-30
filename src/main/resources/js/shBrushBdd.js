/**
 * SyntaxHighlighter brush for BDD grammar.
 * 
 * Adapted from shBrushJava.
 */
SyntaxHighlighter.brushes.Bdd = function()
{
	var keywords =	'Given When Then And Meta @ Narrative Scenario GivenStories Examples Example PENDING NOT PERFORMED FAILED DRY RUN';
	
	this.regexList = [
		{ regex: new RegExp(this.getKeywords(keywords), 'gm'),		css: 'keyword' }		// bdd keyword
		];

	this.forHtmlScript(SyntaxHighlighter.regexLib.aspScriptTags);
};

SyntaxHighlighter.brushes.Bdd.prototype	= new SyntaxHighlighter.Highlighter();
SyntaxHighlighter.brushes.Bdd.aliases		= ['bdd', 'story', 'scenario', 'txt'];
