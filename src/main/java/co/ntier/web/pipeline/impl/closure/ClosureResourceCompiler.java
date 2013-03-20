package co.ntier.web.pipeline.impl.closure;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.extern.slf4j.Slf4j;
import co.ntier.web.pipeline.core.ResourceCompiler;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.WarningLevel;

/**
 * TODO rename this
 * 
 */
@Slf4j
public class ClosureResourceCompiler implements ResourceCompiler{

	private final com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
	private final CompilerOptions options = new CompilerOptions();

	public ClosureResourceCompiler() {
		com.google.javascript.jscomp.Compiler.setLoggingLevel(Level.INFO);

		CompilationLevel.SIMPLE_OPTIMIZATIONS
				.setOptionsForCompilationLevel(options);
		// options.setAliasKeywords(true);
		// options.setAliasAllStrings(true);

		options.setCollapseAnonymousFunctions(true);
		options.setCollapseObjectLiterals(true);
		options.setCollapseProperties(true);
		options.setCollapsePropertiesOnExternTypes(true);
		options.setCollapseVariableDeclarations(true);
		options.setOptimizeArgumentsArray(true);
		options.setOptimizeCalls(true);
		options.setOptimizeParameters(true);
		options.setOptimizeReturns(true);

		options.setAggressiveVarCheck(CheckLevel.OFF);

		VariableRenamingPolicy newVariablePolicy = VariableRenamingPolicy.ALL;
		PropertyRenamingPolicy newPropertyPolicy = PropertyRenamingPolicy.ALL_UNQUOTED;
		options.setRenamingPolicy(newVariablePolicy, newPropertyPolicy);

		options.setAliasExternals(true);
		options.setAliasableGlobals("alert");

		WarningLevel.VERBOSE.setOptionsForWarningLevel(options);
	}
	
	public String compile(List<String> files) {
		return compile(new ArrayList<String>(), files);
	}

	public String compile(List<String> externalJavascriptResources, List<String>primaryJavascriptToCompile){
	    List<SourceFile> externalJavascriptFiles = new ArrayList<SourceFile>();
	    externalJavascriptFiles.addAll( ClosureHelper.getDefaultExterns() );
	    for (String filename : externalJavascriptResources){
	      externalJavascriptFiles.add(SourceFile.fromFile(filename) );
	    }
	    
	    List<SourceFile> primaryJavascriptFiles = new ArrayList<SourceFile>();
	    for (String filename : primaryJavascriptToCompile){
	      primaryJavascriptFiles.add(SourceFile.fromFile(filename) );
	    }
	 
	    compiler.compile(externalJavascriptFiles, primaryJavascriptFiles, options);
	    compiler.optimize();
	 
	    for (JSError message : compiler.getWarnings()){
	    	log.warn("Warning while compiling: {}", message.toString());
	    }
	 
	    for (JSError message : compiler.getErrors()){
	    	log.error("Error while compiling: {}", message.toString());
	    }
	 
	    return compiler.toSource();
	}

}