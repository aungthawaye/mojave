#!/usr/bin/env groovy

//@@formatter:off

import groovy.xml.XmlParser
import groovy.xml.XmlNodePrinter


// Usage: groovy auto-dep.groovy /path/to/project/root
File baseDir = args.length ? new File(args[0]) : new File('.')
if (!baseDir.isDirectory()) {
    System.err.println("Directory not found: ${baseDir}")
    System.exit(1)
}
File rootPomFile = new File(baseDir, 'pom.xml')
if (!rootPomFile.exists()) {
    System.err.println("pom.xml not found in ${baseDir}")
    System.exit(1)
}

def parser = new XmlParser(false, false)
def pomXml = parser.parse(rootPomFile)

def rootPackaging = pomXml.packaging?.text()?.trim() ?: 'pom'
def rootAid       = pomXml.artifactId?.text()?.trim() ?: ''

def ownGid    = pomXml.groupId?.text()?.trim() ?: ''
def parentGid = pomXml.parent?.groupId?.text()?.trim() ?: ''
if (!(ownGid.startsWith('io.mojaloop') || parentGid.startsWith('io.mojaloop'))) {
    System.out.println("Skipping: groupId not in io.mojaloop")
    System.exit(0)
}

def dmNode   = pomXml.dependencyManagement?.getAt(0) ?: pomXml.appendNode('dependencyManagement')
def depsNode = dmNode.dependencies?.getAt(0)             ?: dmNode.appendNode('dependencies')

declarationCleanup:
depsNode.dependency.findAll { dep ->
    dep.groupId.text().startsWith('io.mojaloop')
}.each { dep ->
    depsNode.remove(dep)
}

def addDep = { gid, aid, version ->
    if (!depsNode.dependency.any { dep -> dep.artifactId.text() == aid }) {
        def depNode = depsNode.appendNode('dependency')
        depNode.appendNode('groupId',    gid)
        depNode.appendNode('artifactId', aid)
        depNode.appendNode('version',    version)
    }
}

if (rootPackaging != 'pom' && !rootAid.contains('-service')) {
    addDep(ownGid, rootAid, pomXml.version?.text()?.trim() ?: '')
} else {
    def processModules
    processModules = { xmlNode, dir ->
        xmlNode.modules?.module.each { modNode ->
            def name = modNode.text()?.trim()
            System.out.println("Processing module: ${name}")
            def mDir = new File(dir, name)
            def mPom = new File(mDir, 'pom.xml')
            if (!mPom.exists()) {
                return
            }

            def cXml = parser.parse(mPom)
            def pkg = cXml.packaging?.text()?.trim() ?: 'jar'
            def gid = cXml.groupId?.text()?.trim() ?: cXml.parent?.groupId?.text()?.trim() ?: ownGid
            def aid = cXml.artifactId?.text()?.trim()
            def ver = cXml.version?.text()?.trim() ?: pomXml.version?.text()?.trim() ?: ''

            if (pkg != 'pom' && !aid.contains('-service') && gid.startsWith('io.mojaloop')) {
                addDep(gid, aid, ver)
            }
            processModules(cXml, mDir)
        }
    }
    processModules(pomXml, baseDir)
}

def fw = new FileWriter(rootPomFile)
def printer = new XmlNodePrinter(new PrintWriter(fw))
printer.setPreserveWhitespace(true)
printer.print(pomXml)
fw.close()
System.out.println('Updated dependencyManagement in ' + rootPomFile)
