package com.volarvideo.demoapp.util;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;


public class VVNode implements Element {
	private Element node;
	private String localName;
	private String text;
	
	VVNode(Node n) {
		node = (Element) n;
		localName = XMLParser.localName(n);
		text = XMLParser.getValue(node);
	}
	
	@Override
	public String getLocalName() {
		return localName;
	}
	
	/**Gets the element's content with the white space trimmed from the start/end.
	 * 
	 * @return Trimmed content text
	 */
	public String getValue() {
		return text;
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		return node.appendChild(newChild);
	}

	@Override
	public Node cloneNode(boolean deep) {
		return node.cloneNode(deep);
	}

	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		return node.compareDocumentPosition(other);
	}

	@Override
	public NamedNodeMap getAttributes() {
		return node.getAttributes();
	}

	@Override
	public String getBaseURI() {
		return node.getBaseURI();
	}

	@Override
	public NodeList getChildNodes() {
		return node.getChildNodes();
	}

	@Override
	public Object getFeature(String feature, String version) {
		return node.getFeature(feature, version);
	}

	@Override
	public Node getFirstChild() {
		return node.getFirstChild();
	}

	@Override
	public Node getLastChild() {
		return node.getLastChild();
	}

	@Override
	public String getNamespaceURI() {
		return node.getNamespaceURI();
	}

	@Override
	public Node getNextSibling() {
		return node.getNextSibling();
	}

	@Override
	public String getNodeName() {
		return node.getNodeName();
	}

	@Override
	public short getNodeType() {
		return node.getNodeType();
	}

	@Override
	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}

	@Override
	public Document getOwnerDocument() {
		return node.getOwnerDocument();
	}

	@Override
	public Node getParentNode() {
		return node.getParentNode();
	}

	@Override
	public String getPrefix() {
		return node.getPrefix();
	}

	@Override
	public Node getPreviousSibling() {
		return node.getPreviousSibling();
	}

	@Override
	public String getTextContent() throws DOMException {
		return node.getTextContent();
	}

	@Override
	public Object getUserData(String key) {
		return node.getUserData(key);
	}

	@Override
	public boolean hasAttributes() {
		return node.hasAttributes();
	}

	@Override
	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return node.insertBefore(newChild, refChild);
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return node.isDefaultNamespace(namespaceURI);
	}

	@Override
	public boolean isEqualNode(Node arg) {
		return node.isEqualNode(arg);
	}

	@Override
	public boolean isSameNode(Node other) {
		return node.isSameNode(other);
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return node.lookupNamespaceURI(prefix);
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		return node.lookupPrefix(namespaceURI);
	}

	@Override
	public void normalize() {
		node.normalize();
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		return node.removeChild(oldChild);
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return node.replaceChild(newChild, oldChild);
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		node.setNodeValue(nodeValue);
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		node.setPrefix(prefix);
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		node.setTextContent(textContent);
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return node.setUserData(key, data, handler);
	}

	@Override
	public String getAttribute(String name) {
		return node.getAttribute(name);
	}

	@Override
	public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
		return node.getAttributeNS(namespaceURI, localName);
	}

	@Override
	public Attr getAttributeNode(String name) {
		return node.getAttributeNode(name);
	}

	@Override
	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
		return node.getAttributeNodeNS(namespaceURI, localName);
	}

	@Override
	public NodeList getElementsByTagName(String name) {
		return node.getElementsByTagName(name);
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
		return node.getElementsByTagNameNS(namespaceURI, localName);
	}

	@Override
	public TypeInfo getSchemaTypeInfo() {
		return node.getSchemaTypeInfo();
	}

	@Override
	public String getTagName() {
		return node.getTagName();
	}

	@Override
	public boolean hasAttribute(String name) {
		return node.hasAttribute(name);
	}

	@Override
	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
		return node.hasAttributeNS(namespaceURI, localName);
	}

	@Override
	public void removeAttribute(String name) throws DOMException {
		node.removeAttribute(name);
	}

	@Override
	public void removeAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		node.removeAttributeNS(namespaceURI, localName);
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return node.removeAttributeNode(oldAttr);
	}

	@Override
	public void setAttribute(String name, String value) throws DOMException {
		node.setAttribute(name, value);
	}

	@Override
	public void setAttributeNS(String namespaceURI, String qualifiedName,
			String value) throws DOMException {
		node.setAttributeNS(namespaceURI, qualifiedName, value);
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return node.setAttributeNode(newAttr);
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return node.setAttributeNodeNS(newAttr);
	}

	@Override
	public void setIdAttribute(String name, boolean isId)
			throws DOMException {
		node.setIdAttribute(name, isId);
	}

	@Override
	public void setIdAttributeNS(String namespaceURI, String localName,
			boolean isId) throws DOMException {
		node.setIdAttributeNS(namespaceURI, localName, isId);
	}

	@Override
	public void setIdAttributeNode(Attr idAttr, boolean isId)
			throws DOMException {
		node.setIdAttributeNode(idAttr, isId);
	}
}
