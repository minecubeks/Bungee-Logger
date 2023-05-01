<h1>Bungee status exporter</h1>
<h2>Proxy server status exporter to MySQL</h2>

<h3>Setup:</h3>
1, Setup MySQL database. <br>
2, Create schema named servers_info <br>
3, Put .jar to yours proxy ./plugins folder <br>
4, Edit plugins/Logger/config.json to your MySQL details <br>
5, Restart server <br>

<h3>How can i use it?</h3>
Idk its kinda random but i think it may be useful.

<h3>To view MySQL</h3>

U can use simple SQL query <code><b>SELECT * FROM &#96;proxy&#96;;</b></code> <br>
Or u can select each value using neither Last_Launch, Last_End or status. (status is always 1 or 0, 1=online and 0=offline) </br>
Etc.: <code><b>SELECT Last_End FROM &#96;proxy&#96;;</b></code><br>
