02/18/20 Recitation
==================

17-355/17-665/17-819: Program Analysis (Spring 2022)
----------------------------------------------------

This repository is a recitation template.
The code presented here uses Soot to perform Definitions Analysis, keeping a 
list of all variable definitions with their line numbers.
In this recitation, we consider how to extend it to keeping track of just the
most recent assignment of a variable by modifying the `flowThrough` method in 
`IntraDefsAnalysis`.

## Building

1. Open this repository using **GitHub Codespaces with at least the 8GB VM option**. 
2. Run/test your implementation inside the codespace environment by opening the **Gradle Tab on the sidebar and clicking test**.
3. You can use VSCode's debugger by setting breakpoints on your code, and selecting the **debug option under test on the gradle tab**.

## File Structure

`inputs.DefsTest` is the code we are analyzing.

`IntraDefsAnalysis`, extending Soot's `ForwardFlowAnalysis`, contains the code we're 
interested in changing.

## Dependencies

As of Feb 2022, the latest Soot release that works is v4.2.1, which depends on ASM 8.0.1
and uses a hard-coded ASM API version 8. This restricts analysis of class files that
are compiled with JDK 15 or earlier. So, we cannot do whole program analysis with Java 16
or higher, because the JDK library classes would be in a format that is unsupported by
ASM 8.0.1 and in turn Soot v4.2.1. Simply upgrading the ASM version to ASM 9.1 does not
fix this, because Soot v4.2.1 has hard-coded the use of ASM API version 8 in its use
of ASM ClassVisitor. 

## Resources

[Soot](http://soot-oss.github.io/soot/)

[ForwardFlowAnalysis Javadoc](https://www.sable.mcgill.ca/soot/doc/soot/toolkits/scalar/ForwardFlowAnalysis.html)

[A Survivor's Guide to Java Program Analysis with Soot](https://www.brics.dk/SootGuide/)
