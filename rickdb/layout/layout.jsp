<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><tiles:getAsString name="title" /></title>
</head>
<body>
<table width="100%" border="1">
<tr><td><tiles:insert attribute="header" /></td></tr>
<tr><td><tiles:insert attribute="body" /></td></tr>
</table>
</body>
</html>