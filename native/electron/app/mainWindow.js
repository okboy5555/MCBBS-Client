const events = window.require('events');
const path = window.require('path');
const fs = window.require('fs');

const electron = window.require('electron');
const { ipcRenderer, shell } = electron;
const remote = electron.remote;

import React from "react";
import PropTypes from "prop-types";
import classNames from 'classnames';

import { withStyles } from "@material-ui/core/styles";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import Drawer from "@material-ui/core/Drawer";
import Button from "@material-ui/core/Button";
import List from "@material-ui/core/List";
import Divider from "@material-ui/core/Divider";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import GridList from '@material-ui/core/GridList';
import GridListTile from '@material-ui/core/GridListTile';
import MobileStepper from '@material-ui/core/MobileStepper';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';

import MenuIcon from "@material-ui/icons/Menu";
import CloseIcon from "@material-ui/icons/Close";
import DescriptionIcon from "@material-ui/icons/Description";
import AccountCircleIcon from "@material-ui/icons/AccountCircle";
import WidgetsIcon from "@material-ui/icons/Widgets";
import ListIcon from "@material-ui/icons/List";
import HomeIcon from "@material-ui/icons/Home";
import VoteIcon from "@material-ui/icons/HowToVote";
import NeedHelpIcon from "@material-ui/icons/LiveHelp";
import LeftIcon from "@material-ui/icons/KeyboardArrowLeft";
import RightIcon from "@material-ui/icons/KeyboardArrowRight";
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';

import MainPageTag from "./bin/viewManager/mainPage";

const drawerWidth = 200;

const styles = theme =>({
  root: {
    flexGrow: 1,
    width: '100%',
    height: '100%'
  },
  grow: {
    flexGrow: 1
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  menuButton: {
    marginLeft: 12,
    marginRight: 36,
  },
  hide: {
    display: 'none',
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: 'nowrap',
  },
  drawerOpen: {
    width: drawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  },
  drawerClose: {
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    overflowX: 'hidden',
    width: theme.spacing.unit * 7 + 1,
    [theme.breakpoints.up('sm')]: {
      width: theme.spacing.unit * 9 + 1,
    },
  },
  toolbar: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: '0 8px',
    ...theme.mixins.toolbar,
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing.unit * 3,
  },
  primaryColor: {
    colorPrimary: '#ecdec1'
  }
});

// 标签图标列表
const icons = {
  "主页": <HomeIcon />,
  "帖子": <DescriptionIcon />,
  "投票": <VoteIcon />,
  "悬赏": <NeedHelpIcon />,
}

let tags = [];

// 构建新的标签实体
class Tag {
  constructor(object, where) {
    this.title = object.title == null ? "未知标题" : object.title;
    this.subTitle = object.subTitle == null ? "" : object.subTitle;
    this.icon = object.icon == null ? <DescriptionIcon /> : object.icon;
    this.content = object.content == null ? {} : object.content;

    this.enableClose = object.enableClose == null ? true : object.enableClose;
    this.enableSelect = object.enableSelect == null ? true : object.enableSelect;

    this.titleBold = object.titleBold == null ? false : object.titleBold;
    this.titleItalic = object.titleItalic == null ? false : object.titleItalic;
    this.titleUnderline = object.titleUnderline == null ? false : object.titleUnderline;
    this.titleDeleteline = object.titleDeleteline == null ? false : object.titleDeleteline;
    this.titleColor = object.titleColor == null ? "#000" : object.titleColor;

    if (object.render == null)
      throw Error('未定义标签的渲染器！');
    this.render = object.render;
  }
}

function newTag(object) {
  tags.push(new Tag(object));
}

// 窗口主体
class MainWindow extends React.Component {
  constructor() {
    super();
    this.state = {
      tag: "mainPage",
      tagOpening: false
    };
  }

  handleDrawerOpen() {
    this.setState({ tagOpening: true });
  };

  handleDrawerClose() {
    this.setState({ tagOpening: false });
  };

  render() {
    const { classes, theme } = this.props;

    return (
      <div className={classes.root}>
        <AppBar
          position="fixed"
          className={classNames(classes.appBar, {
            [classes.appBarShift]: this.state.tagOpening,
          })}
        >
          <Toolbar disableGutters={!this.state.tagOpening}>
            <IconButton
              color="inherit"
              aria-label="Open drawer"
              className={classNames(classes.menuButton, {
                [classes.hide]: this.state.tagOpening,
              })}
            >
              <AccountCircleIcon />
            </IconButton>
            <Typography variant="h6" color="inherit" noWrap>
              Unknown Title
            </Typography>
          </Toolbar>
        </AppBar>
        <Drawer
          variant="permanent"
          className={classNames(classes.drawer, {
            [classes.drawerOpen]: this.state.tagOpening,
            [classes.drawerClose]: !this.state.tagOpening,
          })}
          classes={{
            paper: classNames({
              [classes.drawerOpen]: this.state.tagOpening,
              [classes.drawerClose]: !this.state.tagOpening,
            }),
          }}
          open={this.state.tagOpening}
        >
          <div className={classes.toolbar}>
            <IconButton onClick={this.handleDrawerClose}>
              <ChevronLeftIcon />
            </IconButton>
          </div>
          <Divider />
          <div>
            {this.state.tagOpening ? (
              <List>
                {
                  tags.map((n, index) => {
                    return (
                      <ListItem key={index}>
                        {n.icon}
                        <ListItemText primary={n.title} secondary={n.subTitle} />
                        {
                          n.enableClose && (
                            <ListItemSecondaryAction>
                              <IconButton aria-label="关闭">
                                <CloseIcon />
                              </IconButton>
                            </ListItemSecondaryAction>
                          )
                        }
                      </ListItem>
                    )
                  })
                }
              </List>
            ) : (
                <Grid container direction='column' justify='flex-start' alignItems='baseline' className={classes.stretch}>
                  <Grid item xs={1}>
                    <IconButton>
                      <DescriptionIcon onClick={this.handleDrawerOpen} />
                    </IconButton>
                  </Grid>
                  <Grid item xs={1}>
                    <IconButton>
                      <ListIcon />
                    </IconButton>
                  </Grid>
                  <Grid item xs={1}>
                    <IconButton>
                      <WidgetsIcon />
                    </IconButton>
                  </Grid>
                </Grid>
              )}
          </div>
        </Drawer>
        <main className={classes.content}>
          <div className={classes.toolbar} />
          // 主体
        </main>
      </div>
    );
  }
}

MainWindow.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(MainWindow);
