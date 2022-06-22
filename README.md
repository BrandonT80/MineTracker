<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/BrandonT80/MineTracker">
    <!--<img src="images/logo.png" alt="Logo" width="80" height="80">-->
  </a>

  <h3 align="center">MineTracker</h3>

  <p align="center">
    An AntiXRay Tool For Your Server!
    <br />
    <a href="https://github.com/BrandonT80/MineTracker/wiki"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/BrandonT80/MineTracker/issues">Report Bug</a>
    ·
    <a href="https://github.com/BrandonT80/MineTracker/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Product Name Screen Shot][product-screenshot]](https://example.com)

Xray is no new problem for server owners. There will never be a shortage of xrayers on your server and you probably find it to be a pain to try to catch them all. This plugin will help you identify players mining ores that are commonly mined by xrayers, and track the speed at which they obtain the ores. There will always need a person to verify the player as an xray user, but this plugin will help to pinpoint when you should investigate.

Here's why you should use this plugin over other similar ones:
* Minetracker teleports the staff member to the ore mined, not the player who mined it. This allows the staff member to see each individual ore(vein) that was mined at the time of the ping.
* Minetracker puts the staff member into spectator before teleporting. This is something the other plugins fail to do, resulting in potentially tipping off the player that they are being investigated when a staff member randomly teleports to them (see first bullet above).
* Minetracker is now open-source! This means that others can help fix any bugs as well as implement new features, while the other plugins just update to the new minecraft versions instead of improving and innovating. 


<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

The below programs/apis were used to make MineTracker

* [Java](https://www.oracle.com/java/technologies/java-se-glance.html)
* [Eclipse](https://www.eclipse.org/)
* [SpigotMC](https://www.spigotmc.org/)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

Below is how to install and use MineTracker:

### Prerequisites

Currently, the only prerequisites of this plugin is to be on a Spigot server, or a fork of Spigot. However, we might soon be adding back the Essentials plugin dependency for a vanish feature request.


### Installation

1. Simply drag and drop the .jar file into your plugins folder and run the server. 
2. Head over to the generated config file in /MineTracker/config.yml
3. Leave the version alone, set the nopermissions system to true ONLY if you do not have any permissions system, put staff user names in the staffusernames section ONLY if the nopermissions is set to true.
4. If you have nopermissions set to false, make sure your staff members have the mt.staff permission.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

To use, simply wait for a player to mine ores. When they do a message will appear to staff with a teleport link, click the link to teleport to the mined ore. 

Staff can also use "/mt sound" command to turn off the ping sounds.


<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] Make Public
- [x] Make readme
- [ ] Add wiki
- [ ] Add config option for ores selections
- [ ] Change sound options to be per staff member, not for all

See the [open issues](https://github.com/BrandonT80/MineTracker/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "issue".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

BrandonT80 - [Discord](https://discord.gg/s4CCpykGqx)

Project Link: [https://github.com/BrandonT80/MineTracker](https://github.com/BrandonT80/MineTracker)

<p align="right">(<a href="#top">back to top</a>)</p>
